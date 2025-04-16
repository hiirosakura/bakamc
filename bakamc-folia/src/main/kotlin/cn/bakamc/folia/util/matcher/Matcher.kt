package cn.bakamc.folia.util.matcher

interface Matcher<T> {

    fun match(target: T): Boolean

}

interface MatchEntry<T> : Matcher<T> {

    val mode: Boolean

    fun matchWithMode(obj: T): Boolean = match(obj) == mode

}

interface MultiMatcher<T> : Matcher<T> {

    val entries: List<MatchEntry<T>>

    val mode: MatchMode

    override fun match(obj: T): Boolean {
        return when (mode) {
            MatchMode.AnyMatch  -> entries.any { it.matchWithMode(obj) }
            MatchMode.AllMatch  -> entries.all { it.matchWithMode(obj) }
            MatchMode.NoneMatch -> entries.none { it.matchWithMode(obj) }
        }
    }

    enum class MatchMode {
        /**
         *
         * 在任意匹配模式下，只要存在一个匹配项符合条件，则整个匹配规则被视为通过。
         * 该模式常用于需要满足至少一个条件即可的场景。
         *
         */
        AnyMatch,

        /**
         * 当且仅当所有匹配项均不符合指定规则时，匹配才成功。
         * 适用于需要确保没有一项符合条件的场景，例如检测某集合中是否不存在特定属性或值。
         */
        NoneMatch,

        /**
         *  此模式通常用于需要确保全部条件均满足的场景。例如，当需要验证某个集合中所有元素都满足给定规则时，可采用此模式。
         */
        AllMatch;

        companion object {
            fun fromString(str: String): MatchMode {
                return when (str.lowercase()) {
                    "anymatch", "any_match", "||"   -> AnyMatch
                    "nonematch", "none_match", "!=" -> NoneMatch
                    "allmatch", "all_match", "=="   -> AllMatch
                    else                            -> AllMatch
                }
            }
        }
    }

}


