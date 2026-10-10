package io.github.cc007.kolostyles.dsl.font

data class FontWeight(val name: String) {
    init {
        require(isValidName(name)) { "Invalid font-weight theme name: $name" }
    }

    companion object {
        private val namePattern = Regex("[a-z0-9]+(?:-[a-z0-9]+)*")

        internal fun isValidName(name: String): Boolean = namePattern.matches(name)
    }
}
