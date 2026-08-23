package org.mk.papier.ui.tags

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.mk.papier.data.ThemeRepository
import org.mk.papier.model.Theme

class TagsViewModel(application: Application) : AndroidViewModel(application) {

    val themes: List<Theme> = ThemeRepository(application).loadThemes()
}
