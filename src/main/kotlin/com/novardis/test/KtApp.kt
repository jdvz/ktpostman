package com.novardis.test

import com.novardis.test.view.MasterView
import tornadofx.App
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus

class KtApp: App(MasterView::class) {
    companion object {
        @JvmStatic fun main(args: Array<String>) {
            launch<KtApp>(args)
        }
    }

    init {
        reloadStylesheetsOnFocus()
    }
}