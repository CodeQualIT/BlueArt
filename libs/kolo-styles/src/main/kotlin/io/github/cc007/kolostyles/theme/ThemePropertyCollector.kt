package io.github.cc007.kolostyles.theme

import org.springframework.stereotype.Component

@Component
class ThemePropertyCollector {
    fun <T> collect(block: () -> T): Pair<T, List<String>> {
        check(requested.get() == null) { "Theme collection is already active" }
        requested.set(linkedSetOf())
        return try {
            block() to requested.get()!!.toList()
        } finally {
            requested.remove()
        }
    }

    fun request(property: String) {
        requested.get()?.plusAssign(property)
    }

    private companion object {
        val requested = ThreadLocal<LinkedHashSet<String>?>()
    }
}
