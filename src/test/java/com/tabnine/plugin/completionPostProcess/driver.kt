package com.tabnine.plugin.completionPostProcess

import com.tabnine.binary.requests.autocomplete.AutocompleteRequest
import com.tabnine.binary.requests.autocomplete.AutocompleteResponse
import com.tabnine.binary.requests.autocomplete.ResultEntry
import com.tabnine.general.CompletionKind
import junit.framework.TestCase.assertEquals

const val TAB_SIZE = 2

fun assertNewPrefix(response: AutocompleteResponse, text: String) {
    assertEquals(
        "new prefix isn't trimmed correctly",
        text,
        response.results[0].new_prefix
    )
}

fun request(before: String): AutocompleteRequest {
    val request = AutocompleteRequest()
    request.before = before
    return request
}

fun snippetResponse(newPrefix: String): AutocompleteResponse {
    val response = AutocompleteResponse()
    val resultEntry = ResultEntry()
    resultEntry.new_prefix = newPrefix
    resultEntry.completion_kind = CompletionKind.Snippet
    response.results = arrayOf(resultEntry)
    return response
}

fun nonSnippetResponse(newPrefix: String): AutocompleteResponse {
    val response = AutocompleteResponse()
    val resultEntry = ResultEntry()
    resultEntry.new_prefix = newPrefix
    response.results = arrayOf(resultEntry)
    return response
}