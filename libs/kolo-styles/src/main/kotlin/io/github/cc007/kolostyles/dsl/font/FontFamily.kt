package io.github.cc007.kolostyles.dsl.font

data class FontFamily(val name: String) {
    init {
        require(isValidName(name)) { "Invalid or reserved font-family theme name: $name" }
    }

    companion object {
        private val namePattern = Regex("[a-z][a-z0-9]*(?:-[a-z0-9]+)*")
        internal val reservedWeightNames = setOf(
            "thin", "extralight", "light", "normal", "medium", "semibold", "bold", "extrabold", "black",
        )

        internal fun isValidName(name: String): Boolean =
            namePattern.matches(name) && name !in reservedWeightNames && !name.startsWith("weight-")
    }
}
