package com.tabnine.inline.render;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorCustomElementRenderer;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

import static com.tabnine.inline.render.FontFinder.getFont;

public class BlockElementRenderer implements EditorCustomElementRenderer {
    private final Editor editor;
    private final List<String> block;
    private final boolean deprecated;

    public BlockElementRenderer(Editor editor, List<String> block, boolean deprecated) {
        this.editor = editor;
        this.block = block;
        this.deprecated = deprecated;
    }

    @Override
    public int calcWidthInPixels(@NotNull Inlay inlay) {
        String firstLine = block.get(0);
        return editor.getContentComponent()
                .getFontMetrics(getFont(this.editor, this.deprecated)).stringWidth(firstLine);
    }

    @Override
    public int calcHeightInPixels(@NotNull Inlay inlay) {
        return this.editor.getLineHeight() * this.block.size();
    }

    @Override
    public void paint(
            @NotNull Inlay inlay,
            @NotNull Graphics g,
            @NotNull Rectangle targetRegion,
            @NotNull TextAttributes textAttributes) {
        g.setColor(JBColor.GRAY);
        g.setFont(getFont(this.editor, this.deprecated));

        int i = 0;
        for (String line : this.block) {
            g.drawString(
                    line,
                    0,
                    targetRegion.y + i * this.editor.getLineHeight() + this.editor.getAscent());
            i++;
        }
    }
}