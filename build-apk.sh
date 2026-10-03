#!/usr/bin/env bash
# Build a signed, installable release APK. Never installs or runs device tests.
set -euo pipefail
umask 077

project_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
cd "$project_dir"

fail() { printf 'Hata: %s\n' "$*" >&2; exit 1; }

if [[ ${1:-} == --help ]]; then
    printf '%s\n' \
        'Kullanım: ./build-apk.sh' \
        'Çıktı: build/apk/Papier_yyyyMMdd_HHmmss.apk' \
        'İmza anahtarı: .signing/papier-release.p12 (ilk çalıştırmada oluşturulur).' \
        '.signing klasörünü güvenli bir yere yedekleyin; sonraki güncellemelerde aynı anahtar gerekir.'
    exit 0
fi
[[ $# == 0 ]] || fail 'Bilinmeyen seçenek. Kullanım: ./build-apk.sh'

for tool in java keytool openssl flock; do
    command -v "$tool" >/dev/null || fail "$tool bulunamadı. Java JDK, OpenSSL ve util-linux kurulu olmalı."
done

sdk_dir="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"
if [[ -z "$sdk_dir" && -f local.properties ]]; then
    while IFS= read -r line || [[ -n "$line" ]]; do
        if [[ "$line" == sdk.dir=* ]]; then
            sdk_dir="${line#sdk.dir=}"
            sdk_dir="${sdk_dir%$'\r'}"
            sdk_dir="${sdk_dir//\\:/:}"
            sdk_dir="${sdk_dir//\\ / }"
            break
        fi
    done < local.properties
fi
[[ -n "$sdk_dir" ]] || sdk_dir="$HOME/Android/Sdk"
[[ -d "$sdk_dir/build-tools" ]] || fail 'Android SDK bulunamadı. local.properties veya ANDROID_HOME ayarlayın.'

build_tools=""
while IFS= read -r version; do
    candidate="$sdk_dir/build-tools/$version"
    if [[ -x "$candidate/apksigner" && -x "$candidate/zipalign" ]]; then
        build_tools="$candidate"
        break
    fi
done < <(find "$sdk_dir/build-tools" -mindepth 1 -maxdepth 1 -type d -printf '%f\n' | sort -Vr)
[[ -n "$build_tools" ]] || fail 'Android SDK Build Tools (apksigner ve zipalign) bulunamadı.'

mkdir -p .signing build/apk
chmod 700 .signing
# Prevent two builds from generating different keys or overwriting the same APK.
exec 9>.signing/build.lock
flock -n 9 || fail 'Başka bir APK oluşturma işlemi çalışıyor.'

keystore="$project_dir/.signing/papier-release.p12"
password_file="$project_dir/.signing/password"
if [[ ! -e "$keystore" && ! -e "$password_file" ]]; then
    printf 'İlk kullanım: kalıcı release imza anahtarı oluşturuluyor...\n'
    openssl rand -hex 32 > "$password_file"
    keytool -genkeypair -noprompt \
        -keystore "$keystore" -storetype PKCS12 -alias papier \
        -storepass:file "$password_file" -keypass:file "$password_file" \
        -keyalg RSA -keysize 3072 -validity 10000 \
        -dname 'CN=Papier, O=Papier, C=NL'
elif [[ ! -s "$keystore" || ! -s "$password_file" ]]; then
    fail '.signing eksik veya bozuk. Mevcut anahtarı ve parolayı yedekten geri yükleyin; yeni anahtarla değiştirmeyin.'
fi
chmod 600 "$keystore" "$password_file"
# Fail before building if the saved credentials cannot open the key.
keytool -list -keystore "$keystore" -alias papier -storepass:file "$password_file" >/dev/null

printf 'Release APK derleniyor...\n'
./gradlew :app:assembleRelease --console=plain
unsigned_apk="$project_dir/app/build/outputs/apk/release/app-release-unsigned.apk"
[[ -s "$unsigned_apk" ]] || fail "İmzasız APK bulunamadı: $unsigned_apk"

work_dir="$(mktemp -d "$project_dir/build/apk/.work.XXXXXX")"
trap 'rm -rf -- "$work_dir"' EXIT
printf 'APK hizalanıyor ve imzalanıyor...\n'
"$build_tools/zipalign" -P 16 -f 4 "$unsigned_apk" "$work_dir/aligned.apk"
"$build_tools/apksigner" sign \
    --ks "$keystore" --ks-key-alias papier \
    --ks-pass "file:$password_file" \
    --v4-signing-enabled false \
    --out "$work_dir/Papier.apk" "$work_dir/aligned.apk"
"$build_tools/zipalign" -c -P 16 4 "$work_dir/Papier.apk"
"$build_tools/apksigner" verify --verbose --print-certs "$work_dir/Papier.apk"

# Publish only a fully verified APK, using the computer's local time.
apk_name="Papier_$(date +%Y%m%d_%H%M%S).apk"
output_apk="$project_dir/build/apk/$apk_name"
[[ ! -e "$output_apk" ]] || fail "Aynı zaman damgalı APK zaten var: $output_apk"
mv -- "$work_dir/Papier.apk" "$output_apk"
printf '\nHazır: %s\n' "$output_apk"
printf 'İmza anahtarının ve parolanın bulunduğu .signing/ klasörünü güvenli bir yere yedekleyin.\n'
