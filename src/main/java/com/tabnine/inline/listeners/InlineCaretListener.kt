package com.tabnine.inline.listeners

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.util.Disposer
import com.tabnine.inline.CompletionPreview

class InlineCaretListener(private val completionPreview: CompletionPreview) : CaretListener, Disposable {
    init {
        completionPreview.editor.caretModel.addCaretListener(this)
        Disposer.register(completionPreview, this)
    }

    override fun caretPositionChanged(event: CaretEvent) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return
        }

        Logger.getInstance(javaClass).warn("BOAZ: CaretPositionChanged disposing completion")
        Disposer.dispose(completionPreview)
    }

    override fun dispose() {
        Logger.getInstance(javaClass).warn("BOAZ: CaretPositionChanged disposed")
        completionPreview.editor.caretModel.removeCaretListener(this)
    }
}
