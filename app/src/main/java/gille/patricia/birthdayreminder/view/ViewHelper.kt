package gille.patricia.birthdayreminder.view

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

object StaticViewHelperFunctions {
    fun hideKeyboardInFragment(view: View) {
        ViewCompat.getWindowInsetsController(view)
            ?.hide(WindowInsetsCompat.Type.ime())
    }
}
