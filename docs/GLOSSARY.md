# Glossary

## Application Terms

### ATProto
Decentralized social networking protocol used by Bluesky.

### Bluesky
Social platform built on ATProto.

### CID
Content identifier used to reference immutable data.

### URI (ATProto post URI)
Canonical post identity used for reliable fetch and thread lookup.

### Feed Card
A compact timeline item rendered on `/browse`.

### Localhost Dummy Network
The reserved `localhost` login-network option that routes form login through the in-app dummy ATProto controllers.

### Art Detail Page
Route `/art/{cid}` that renders the primary embed, post text, and comments.

### `kotlinx.html`
Kotlin DSL used in this project to render server-side HTML.

### AI Task
A repository-tracked work item executed by an AI assistant, documented with status, progress notes, and completion details.

### AI Task Index
`docs/AI_TASKS.md`, the status board that links active and completed task records in `docs/ai-tasks/`.

---

## Kolo Styling Library (`:libs:kolo-styles`)

Terms specific to the co-located utility styling infrastructure. See also [docs/DECISIONS.md § D-007](DECISIONS.md).

### `kolo { }`
Element-attached DSL extension (`fun HTMLTag.kolo(block: KoloScope.() -> Unit)`) that records utility tokens into the active request-scoped context and attaches generated class names to the current HTML element. No-ops silently when called outside a `renderKoloHtml` context.

### `KoloScope`
DSL receiver inside a `kolo { }` block. Records base tokens directly into the sink and provides `variant(name)` to enter a variant scope. Does not hold variant state itself.

### `KoloVariantScope`
DSL receiver returned by `KoloScope.variant()` or `KoloVariantScope.variant()`. Owns the accumulated variant chain and prepends it when `recordBase(token)` is called.

### Canonical Token / Canonicalization
The normalization step applied to collected Kolo tokens before the `kolo.css` URL is emitted: trim, drop empties, reject `;` and `[…]` tokens, deduplicate, sort by `(group, variantCount, variantChain, baseUtility, token)`, and join with `;`.

### `renderKoloHtml`
Wrapper around `createHTML()` that installs a request-scoped `KoloRenderContext` (via `ThreadLocal`), runs the HTML block, then replaces the `koloStylesheetLink()` placeholder with the final canonicalized `/css/generated/kolo.css?version=…&kolo=…` URL.

### `koloStylesheetLink()`
`HEAD` extension that emits a `<link rel="stylesheet">` pointing to the current render context's placeholder, which `renderKoloHtml` replaces post-render with the finalized kolo.css href.

### Spacing Utilities
Margin/padding typed DSL helpers (`m()`, `mt()`, `mb()`, `ml()`, `mr()`, `mx()`, `my()`, `p()`, `pt()`, `pb()`, `pl()`, `pr()`, `px()`, `py()`) available on both `KoloScope` and `KoloVariantScope` via `SpacingDsl.kt`. Each function records a Tailwind-style token (e.g., `m(0)` → `"m-0"`, `p(4)` → `"p-4"`) and generates a `k-`-prefixed CSS class (e.g., `.k-m-0 { margin: 0; }`).

### Display Utilities
Typed display DSL helpers available on both `KoloScope` and `KoloVariantScope` via `kolostyles.dsl.layout.display.DisplayDsl.kt`. Helpers map one-to-one to Tailwind-compatible tokens (for example `inlineBlock` → `inline-block`, `inlineFlex` → `inline-flex`, `tableRowGroup` → `table-row-group`, `hidden` → `hidden` which generates `display: none`).

### Layout Utilities
Typed layout DSL helpers available on both `KoloScope` and `KoloVariantScope` via `kolostyles.dsl.layout.*`, including:
- box sizing (`boxBorder`, `boxContent`)
- overflow (`overflowHidden`, `overflowXAuto`, `overflowYScroll`, etc.)
- position (`static`, `relative`, `absolute`, `fixed`, `sticky`)
- offsets under `kolostyles.dsl.layout.offset` (`top()`, `insetX()`, `leftAuto`, etc.)
- z-index (`zAuto`, `z(<positive-int>)`)
- object-fit (`objectContain`, `objectCover`, `objectFill`, `objectNone`, `objectScaleDown`)

### Font Utilities
Typed typography DSL helpers available on both `KoloScope` and `KoloVariantScope` via `FontDsl.kt`, covering Tailwind-compatible family (`font-sans`, `font-serif`, `font-mono`), size (`text-xs` … `text-9xl`), and weight (`font-thin` … `font-black`) tokens.

### Sizing Utilities
Typed sizing DSL helpers available on both `KoloScope` and `KoloVariantScope` via `SizingDsl.kt`, covering width/height/min/max utility families (`w-*`, `h-*`, `min-w-*`, `max-w-*`, `min-h-*`, `max-h-*`, and `size-*`) with allow-listed named, numeric, breakpoint, and fractional tokens.

### `SpacingParserHook` / `SpacingGeneratorHook`
Spring `@Component` implementations that provide spacing token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for the production compiler pipeline.

### `layout.display.DisplayParserHook` / `layout.display.DisplayGeneratorHook`
Spring `@Component` implementations that provide display token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for the production compiler pipeline.

### `LayoutParserHook` / `LayoutGeneratorHook`
Spring `@Component` implementations that provide non-display layout token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for box-sizing, overflow, position, z-index, and object-fit utilities.

### `layout.offset.OffsetParserHook` / `layout.offset.OffsetGeneratorHook`
Spring `@Component` implementations that provide offset layout token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for `inset`, `inset-x`, `inset-y`, `top`, `right`, `bottom`, and `left` utilities.

### `FontParserHook` / `FontGeneratorHook`
Spring `@Component` implementations that provide font token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for the production compiler pipeline.

### `SizingParserHook` / `SizingGeneratorHook`
Spring `@Component` implementations that provide sizing token parsing (`StyleParserHook`) and CSS rule generation (`StyleGeneratorHook`) for the production compiler pipeline.

### `KoloCssCompiler` bean wiring
`KoloCssCompiler` is a Spring `@Service` that receives injected `List<StyleParserHook>` and `List<StyleGeneratorHook>`; Spring discovers hook implementations (including spacing and display hooks) as beans and supplies them automatically.

### Responsive Variant
A named Kolo scope that qualifies a utility by viewport range. `sm` through `2xl` generate minimum-width media queries; `max-sm` through `max-2xl` generate exclusive maximum-width queries at 639.98px, 767.98px, 1023.98px, 1279.98px, and 1535.98px.

### `kolo-exception`
An inline page-CSS marker for behavior that Kolo cannot represent without changing semantics. The browse 640px and 960px rules and art 700px rules remain exceptions because their inclusive or application-specific breakpoints do not equal a named exclusive Kolo range.
