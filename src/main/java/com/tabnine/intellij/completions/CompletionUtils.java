package com.tabnine.intellij.completions;

import static com.tabnine.general.StaticConfig.MAX_COMPLETIONS;
import static com.tabnine.general.Utils.endsWithADot;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.tabnine.binary.requests.autocomplete.AutocompleteResponse;
import com.tabnine.binary.requests.autocomplete.ResultEntry;
import com.tabnine.binary.requests.autocomplete.SnippetContext;
import com.tabnine.general.CompletionKind;
import com.tabnine.prediction.TabNineCompletion;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class CompletionUtils {

  private static String getCursorPrefix(@NotNull Document document, int cursorPosition) {
    int lineNumber = document.getLineNumber(cursorPosition);
    int lineStart = document.getLineStartOffset(lineNumber);

    return document.getText(TextRange.create(lineStart, cursorPosition)).trim();
  }

  private static String getCursorSuffix(@NotNull Document document, int cursorPosition) {
    int lineNumber = document.getLineNumber(cursorPosition);
    int lineEnd = document.getLineEndOffset(lineNumber);

    return document.getText(TextRange.create(cursorPosition, lineEnd)).trim();
  }

  @NotNull
  public static TabNineCompletion createTabnineCompletion(
      @NotNull Document document,
      int offset,
      String oldPrefix,
      ResultEntry result,
      int index,
      SnippetContext snippetContext) {
    TabNineCompletion completion =
        new TabNineCompletion(
            oldPrefix,
            result.new_prefix,
            result.old_suffix,
            result.new_suffix,
            index,
            CompletionUtils.getCursorPrefix(document, offset),
            CompletionUtils.getCursorSuffix(document, offset),
            result.origin,
            result.completion_kind,
            result.is_cached,
            snippetContext);

    completion.detail = result.detail;

    if (result.deprecated != null) {
      completion.deprecated = result.deprecated;
    }

    return completion;
  }

  static int completionLimit(
      CompletionParameters parameters, CompletionResultSet result, boolean isLocked) {
    return completionLimit(
        parameters.getEditor().getDocument(),
        result.getPrefixMatcher().getPrefix(),
        parameters.getOffset(),
        isLocked);
  }

  public static int completionLimit(
      @NotNull Document document, @NotNull String prefix, int offset, boolean isLocked) {
    if (isLocked) {
      return 1;
    }
    boolean preferTabNine = !endsWithADot(document, offset - prefix.length());

    return preferTabNine ? MAX_COMPLETIONS : 1;
  }

  public static boolean hasSnippetCompletions(AutocompleteResponse completions) {
    return Arrays.stream(completions.results)
        .anyMatch(result -> result.completion_kind == CompletionKind.Snippet);
  }

  public static boolean hasSnippetCompletions(List<TabNineCompletion> completions) {
    return completions.stream()
        .anyMatch(completion -> completion.completionKind == CompletionKind.Snippet);
  }
}
