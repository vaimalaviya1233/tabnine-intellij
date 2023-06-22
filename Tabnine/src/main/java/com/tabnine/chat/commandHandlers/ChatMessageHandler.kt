package com.tabnine.chat.commandHandlers

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project

abstract class ChatMessageHandler<RequestPayload, ResponsePayload>(protected val gson: Gson) {
    fun handleRaw(data: JsonElement?, project: Project): ResponsePayload? {
        val payload = deserializeRequest(data)
        return handle(payload, project)
    }

    protected fun getEditorFromProject(project: Project): Editor? {
        val fileEditor = FileEditorManager.getInstance(project).selectedEditor ?: return null
        val dataContext = DataManager.getInstance().getDataContext(fileEditor.component)

        return CommonDataKeys.EDITOR.getData(dataContext)
    }

    abstract fun handle(payload: RequestPayload?, project: Project): ResponsePayload?

    abstract fun deserializeRequest(data: JsonElement?): RequestPayload?
}
