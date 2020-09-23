@file:Suppress("UNUSED_PARAMETER")

package lesson7

import java.lang.Integer.max

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
//время: O(first.len*second.len)
//память: O(first.len*second.len)
fun longestCommonSubSequence(first: String, second: String): String {
    if (first == "" || second == "")
        return ""
    val matrix = Array(first.length + 1) { IntArray(second.length + 1) }
    for (i in first.length - 1 downTo 0) {
        for (j in second.length - 1 downTo 0) {
            matrix[i][j] =
                if (first[i] == second[j])
                    matrix[i + 1][j + 1] + 1
                else
                    max(matrix[i + 1][j], matrix[i][j + 1])
        }
    }
    var i = 0
    var j = 0
    return buildString {
        while (matrix[i][j] != 0 && i < first.length && j < second.length) {
            if (first[i] == second[j]) {
                append(first[i])
                i++
                j++
            } else {
                if (matrix[i][j] == matrix[i + 1][j])
                    i++
                else
                    j++
            }
        }
    }
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
//время: O(N^2)
//память: O(N)
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    val size = list.size
    if (size < 1)
        return list
    val previous = MutableList(size) { -1 }
    val d = MutableList(size) { 1 }
    for (i in list.indices) {
        for (j in 0 until i) {
            if (list[j] < list[i] && d[j] + 1 > d[i]) {
                d[i] = d[j] + 1
                previous[i] = j
            }
        }
    }

    val len = d.max()
    var pos = d.indexOf(len)

    val result = mutableListOf<Int>()
    while (pos != -1) {
        result.add(0, list[pos])
        pos = previous[pos]
    }
    return result
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5