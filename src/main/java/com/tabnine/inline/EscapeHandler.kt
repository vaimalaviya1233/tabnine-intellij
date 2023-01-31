package com.tabnine.inline

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.tabnine.binary.requests.notifications.shown.SuggestionDroppedReason
import com.tabnine.general.DependencyContainer
import com.tabnine.prediction.CompletionFacade

class EscapeHandler(private val myOriginalHandler: EditorActionHandler) : EditorActionHandler() {
    private val completionsEventSender = DependencyContainer.instanceOfCompletionsEventSender()

    public override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext) {
        sendSuggestionDroppedEvent(editor)
        CompletionPreview.clear(editor)
        if (myOriginalHandler.isEnabled(editor, caret, dataContext)) {
            myOriginalHandler.execute(editor, caret, dataContext)
        }
    }

    private fun sendSuggestionDroppedEvent(editor: Editor) {
        val currentCompletion = CompletionPreview.getCurrentCompletion(editor) ?: return

        try {
            val netLength = currentCompletion.netLength
            val filename = CompletionFacade.getFilename(FileDocumentManager.getInstance().getFile(editor.document))
            val metadata = currentCompletion.completionMetadata

            completionsEventSender.sendSuggestionDropped(netLength, filename, SuggestionDroppedReason.ManualCancel, metadata)
        } catch (e: Throwable) {
            Logger.getInstance(javaClass).warn("Escape listener failed to send suggestion dropped event", e)
        }
    }

    public override fun isEnabledForCaret(
        editor: Editor,
        caret: Caret,
        dataContext: DataContext
    ): Boolean {
        val preview = CompletionPreview.getInstance(editor)
        return if (preview != null) {
            true
        } else myOriginalHandler.isEnabled(editor, caret, dataContext)
    }

    companion object {
        const val ACTION_ID = "EditorEscape"
    }
}