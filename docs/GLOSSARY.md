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
Typed typography DSL helpers available on both `KoloScope` and `KoloVariantScope` via `FontDsl.kt`, covering heavily Tailwind-inspired family, size (`text-xs` … `text-9xl`), and weight tokens. Existing family helpers cover sans/serif/mono; `fontFamily(FontFamily("display"))` names a contributed `--k-font-display` entry. Existing weight helpers cover thin through black; `fontWeight(FontWeight("book"))` names a contributed `--k-font-weight-book` entry, and numeric names require explicit registration. Family names exclude reserved standard weight names and metadata suffixes; eligible family/weight collisions fail startup. Size utilities include their baseline-defined theme companion line-height. All font utility values come from the effective theme.

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
A typed Kolo scope that qualifies a utility by viewport range. `sm`, `md`, `lg`, `xl`, and `x2l` generate `width >=` queries at 40rem, 48rem, 64rem, 80rem, and 96rem; `maxSm` through `max2xl` generate exclusive `width <` queries at those thresholds. State scopes are `hover`, `focus`, `focusVisible`, `active`, and `visited`.

### Effective Kolo Theme
The immutable startup-composed theme used by Kolo generators. It contains the generic version-pinned `--k-*` baseline plus ordered consumer contributions; it is not discovered or changed during a CSS request.

### Theme Contribution
An application/library-owned, identified and precedence-ordered set of theme entries and optional keyframes. Equal-precedence conflicting values are startup errors.

### Theme Property Collector
The per-compilation collector used by a supported generator to request an exact effective-theme property. It produces one deterministic minimal `:root` block before utility rules, and emits an associated keyframe only once.

### BlueArt Semantic Theme Role
An application-owned `--k-color-blueart-*` entry describing a visual purpose rather than a raw color. Current roles map page background `#c2dff5`, surfaces `#d4eaf9`/`#dff1fe`, author surface `#eaf6ff`, border `#93b9d9`, primary/action `#0f85fe`, accent `#0a62a8`, banner `#0a5fa8`, body text `#0e2333`, muted text `#3d6680`, link/hashtag `#085898`, banner foreground `#f2f8ff`, and action foreground `#eef6ff`.

### Bounded Alias Migration
Page CSS may define a legacy-property alias only while a matching Kolo utility guarantees canonical property emission. Kolo never emits legacy aliases, and aliases are removed once no generated page stylesheet references them.

### `kolo-exception`
An inline page-CSS marker for behavior that Kolo cannot represent without changing semantics. The browse 640px and 960px rules and art 700px rules remain exceptions because their inclusive or application-specific breakpoints do not equal a named exclusive Kolo range.
