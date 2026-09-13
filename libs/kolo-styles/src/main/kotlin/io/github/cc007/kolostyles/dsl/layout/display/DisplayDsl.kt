package io.github.cc007.kolostyles.dsl.layout.display

import io.github.cc007.kolostyles.dsl.KoloScope
import io.github.cc007.kolostyles.dsl.KoloVariantScope

private fun KoloScope.recordDisplay(token: String) {
    recordBase(token)
}

private fun KoloVariantScope.recordDisplay(token: String) {
    recordBase(token)
}

val KoloScope.block: Unit get() = recordDisplay("block")
val KoloScope.`inline`: Unit get() = recordDisplay("inline")
val KoloScope.inlineBlock: Unit get() = recordDisplay("inline-block")
val KoloScope.flowRoot: Unit get() = recordDisplay("flow-root")
val KoloScope.flex: Unit get() = recordDisplay("flex")
val KoloScope.inlineFlex: Unit get() = recordDisplay("inline-flex")
val KoloScope.grid: Unit get() = recordDisplay("grid")
val KoloScope.inlineGrid: Unit get() = recordDisplay("inline-grid")
val KoloScope.contents: Unit get() = recordDisplay("contents")
val KoloScope.listItem: Unit get() = recordDisplay("list-item")
val KoloScope.hidden: Unit get() = recordDisplay("hidden")
val KoloScope.table: Unit get() = recordDisplay("table")
val KoloScope.inlineTable: Unit get() = recordDisplay("inline-table")
val KoloScope.tableCaption: Unit get() = recordDisplay("table-caption")
val KoloScope.tableCell: Unit get() = recordDisplay("table-cell")
val KoloScope.tableColumn: Unit get() = recordDisplay("table-column")
val KoloScope.tableColumnGroup: Unit get() = recordDisplay("table-column-group")
val KoloScope.tableHeaderGroup: Unit get() = recordDisplay("table-header-group")
val KoloScope.tableRowGroup: Unit get() = recordDisplay("table-row-group")
val KoloScope.tableRow: Unit get() = recordDisplay("table-row")
val KoloScope.tableFooterGroup: Unit get() = recordDisplay("table-footer-group")

val KoloVariantScope.block: Unit get() = recordDisplay("block")
val KoloVariantScope.`inline`: Unit get() = recordDisplay("inline")
val KoloVariantScope.inlineBlock: Unit get() = recordDisplay("inline-block")
val KoloVariantScope.flowRoot: Unit get() = recordDisplay("flow-root")
val KoloVariantScope.flex: Unit get() = recordDisplay("flex")
val KoloVariantScope.inlineFlex: Unit get() = recordDisplay("inline-flex")
val KoloVariantScope.grid: Unit get() = recordDisplay("grid")
val KoloVariantScope.inlineGrid: Unit get() = recordDisplay("inline-grid")
val KoloVariantScope.contents: Unit get() = recordDisplay("contents")
val KoloVariantScope.listItem: Unit get() = recordDisplay("list-item")
val KoloVariantScope.hidden: Unit get() = recordDisplay("hidden")
val KoloVariantScope.table: Unit get() = recordDisplay("table")
val KoloVariantScope.inlineTable: Unit get() = recordDisplay("inline-table")
val KoloVariantScope.tableCaption: Unit get() = recordDisplay("table-caption")
val KoloVariantScope.tableCell: Unit get() = recordDisplay("table-cell")
val KoloVariantScope.tableColumn: Unit get() = recordDisplay("table-column")
val KoloVariantScope.tableColumnGroup: Unit get() = recordDisplay("table-column-group")
val KoloVariantScope.tableHeaderGroup: Unit get() = recordDisplay("table-header-group")
val KoloVariantScope.tableRowGroup: Unit get() = recordDisplay("table-row-group")
val KoloVariantScope.tableRow: Unit get() = recordDisplay("table-row")
val KoloVariantScope.tableFooterGroup: Unit get() = recordDisplay("table-footer-group")
