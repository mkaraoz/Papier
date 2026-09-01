package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import org.mk.papier.model.PronounFile
import org.mk.papier.model.PronounGroup

class PronounRepository(private val context: Context) {

    /** Groups keep their order from pronouns.json — subject first, then the rest. */
    fun loadGroups(): List<PronounGroup> {
        val json = context.assets.open(PRONOUNS_FILE).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, PronounFile::class.java).groups
    }

    companion object {
        private const val PRONOUNS_FILE = "pronouns.json"
    }
}
