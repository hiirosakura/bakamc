package cn.bakamc.folia.util.matcher

class SimpleMatcher(
    override val entries: List<SimpleMatchEntry>,
    override val mode: MultiMatcher.MatchMode
) : MultiMatcher<String> {

    companion object {
        fun parse(str: String): SimpleMatcher {
            runCatching {
                val strings = str.split(";")
                return if (strings.size > 1) {
                    SimpleMatcher(strings[1].split(",").map { SimpleMatchEntry.parse(it) }, MultiMatcher.MatchMode.fromString(strings[0]))
                } else {
                    SimpleMatcher(strings[0].split(",").map { SimpleMatchEntry.parse(it) }, MultiMatcher.MatchMode.AllMatch)
                }
            }
            return SimpleMatcher(listOf(SimpleMatchEntry("any", true)), MultiMatcher.MatchMode.AllMatch)
        }
    }

}

data class SimpleMatchEntry(val type: String, override val mode: Boolean) : MatchEntry<String> {

    companion object {
        fun parse(str: String): SimpleMatchEntry {
            runCatching {
                return if (str.startsWith("!")) {
                    SimpleMatchEntry(str.substring(1), false)
                } else {
                    SimpleMatchEntry(str, true)
                }
            }
            return SimpleMatchEntry(str, true)
        }

    }

    override fun match(target: String): Boolean {
        if (type == "any") return true
        return target == type
    }

}