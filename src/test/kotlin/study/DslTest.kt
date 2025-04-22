package study

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DslTest {
    @ValueSource(strings = ["홍길동", "김철수"])
    @ParameterizedTest
    fun name(name: String) {
        val person =
            introduce {
                name(name)
            }
        assertThat(person.name).isEqualTo(name)
    }

    @Test
    fun company() {
        val person =
            introduce {
                name("홍길동")
                company("hyundai")
            }
        assertThat(person.name).isEqualTo("홍길동")
        assertThat(person.company).isEqualTo("hyundai")
    }

    @Test
    fun skills() {
        val person =
            introduce {
                name("홍길동")
                company("hyundai")
                skills {
                    soft("A passion for problem solving")
                    soft("Good communication skills")
                    hard("Kotlin")
                }
            }
        assertThat(person.skills).contains(
            SkillType.SOFT to "A passion for problem solving",
            SkillType.SOFT to "Good communication skills",
        )
        assertThat(person.skills).contains(SkillType.HARD to "Kotlin")
    }

    @Test
    fun languages() {
        val person =
            introduce {
                name("홍길동")
                company("hyundai")
                skills {
                    soft("A passion for problem solving")
                    soft("Good communication skills")
                    hard("Kotlin")
                }
                languages {
                    "Korean" level 5
                    "English" level 3
                }
            }
        assertThat(person.languages["Korean"]).isEqualTo(5)
        assertThat(person.languages["English"]).isEqualTo(3)

        println()
    }
}

private fun introduce(block: PersonBuilder.() -> Unit): Person {
    return PersonBuilder().apply(block)
        .build()
}

class PersonBuilder(
    var name: String = "",
    var company: String = "",
    val skills: MutableList<Pair<SkillType, String>> = mutableListOf(),
    val languages: MutableMap<String, Int> = mutableMapOf(),
) {
    fun name(name: String) {
        this.name = name
    }

    fun company(company: String) {
        this.company = company
    }

    fun soft(skill: String) {
        skills.add(SkillType.SOFT to skill)
    }

    fun hard(skill: String) {
        skills.add(SkillType.HARD to skill)
    }

    fun skills(block: () -> Unit) {
        block.invoke()
    }

    fun languages(block: LanguagesBuilder.() -> Unit) {
        languages.putAll(LanguagesBuilder().apply(block).languages)
    }

    fun build(): Person {
        return Person(name, company, skills, languages)
    }
}

class LanguagesBuilder(
    val languages: MutableMap<String, Int> = mutableMapOf(),
) {
    infix fun String.level(level: Int) {
        languages[this] = level
    }
}

class Person(
    val name: String,
    val company: String,
    val skills: MutableList<Pair<SkillType, String>>,
    val languages: Map<String, Int>,
)

enum class SkillType {
    SOFT,
    HARD,
}
