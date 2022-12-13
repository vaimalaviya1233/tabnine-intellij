package com.tabnine.inline.listeners

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.util.Disposer
import com.tabnine.inline.CompletionPreview
import com.tabnine.inline.InlineCompletionCache

class InlineCaretListener(private val completionPreview: CompletionPreview) : CaretListener, Disposable {
    init {
        Disposer.register(completionPreview, this)
        completionPreview.editor.caretModel.addCaretListener(this)
    }

    override fun caretPositionChanged(event: CaretEvent) {
        if (ApplicationManager.getApplication().isUnitTestMode) {
            return
        }
        if (isSingleOffsetChange(event)) {
            return
        }

        Disposer.dispose(completionPreview)
        InlineCompletionCache.instance.clear(event.editor)
    }

    private fun isSingleOffsetChange(event: CaretEvent): Boolean {
        return event.oldPosition.line == event.newPosition.line && event.oldPosition.column + 1 == event.newPosition.column
    }

    override fun dispose() {
        completionPreview.editor.caretModel.removeCaretListener(this)
    }
}
