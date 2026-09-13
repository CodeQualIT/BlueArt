package io.github.cc007.kolostyles.compiler.layout.display

import io.github.cc007.kolostyles.compiler.StyleParserHook
import io.github.cc007.kolostyles.compiler.Token
import io.github.cc007.kolostyles.compiler.parseKoloVariants
import kotlinx.css.Display
import org.springframework.stereotype.Component

private val DISPLAY_UTILITY_VALUES: Map<String, Display> = linkedMapOf(
    "block" to Display.block,
    "inline" to Display.inline,
    "inline-block" to Display.inlineBlock,
    "flow-root" to Display.flowRoot,
    "flex" to Display.flex,
    "inline-flex" to Display.inlineFlex,
    "grid" to Display.grid,
    "inline-grid" to Display.inlineGrid,
    "contents" to Display.contents,
    "list-item" to Display.listItem,
    "hidden" to Display.none,
    "table" to Display.table,
    "inline-table" to Display.inlineTable,
    "table-caption" to Display.tableCaption,
    "table-cell" to Display.tableCell,
    "table-column" to Display.tableColumn,
    "table-column-group" to Display.tableColumnGroup,
    "table-header-group" to Display.tableHeaderGroup,
    "table-row-group" to Display.tableRowGroup,
    "table-row" to Display.tableRow,
    "table-footer-group" to Display.tableFooterGroup,
)

@Component
class DisplayParserHook : StyleParserHook {
    override fun parse(token: String): Token? {
        val parts = token.split(':')
        if (parts.any { it.isBlank() }) {
            return null
        }

        val utility = parts.last()
        val displayValue = DISPLAY_UTILITY_VALUES[utility] ?: return null
        val variants = parts.dropLast(1)

        val parsedVariants = parseKoloVariants(variants) ?: return null

        return DisplayToken(
            raw = token,
            stateVariants = parsedVariants.stateVariants,
            mediaVariant = parsedVariants.mediaVariant,
            utility = utility,
            displayValue = displayValue,
        )
    }
}
