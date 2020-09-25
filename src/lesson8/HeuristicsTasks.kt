@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson8

import lesson6.Graph
import lesson6.Path
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import kotlin.math.pow

// Примечание: в этом уроке достаточно решить одну задачу

/**
 * Решить задачу о ранце (см. урок 6) любым эвристическим методом
 *
 * Очень сложная
 *
 * load - общая вместимость ранца, items - список предметов
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
fun fillKnapsackHeuristics(load: Int, items: List<Item>, vararg parameters: Any): Fill {
    TODO()
}

/**
 * Решить задачу коммивояжёра (см. урок 5) методом колонии муравьёв
 * или любым другим эвристическим методом, кроме генетического и имитации отжига
 * (этими двумя методами задача уже решена в под-пакетах annealing & genetic).
 *
 * Очень сложная
 *
 * Граф передаётся через получатель метода
 *
 * Используйте parameters для передачи дополнительных параметров алгоритма
 * (не забудьте изменить тесты так, чтобы они передавали эти параметры)
 */
//время: O(antNumber * iterationNumber * кол-во вершин графа)
//память: O(antNumber * (кол-во вершин графа + кол-во рёбер графа))
fun Graph.findVoyagingPathHeuristics(
    antNumber: Int,
    iterationNumber: Int,
    alpha: Double = -4.0,
    beta: Double = 1.0,
    startedPheromone: Double = 1.0,
    ro: Double = 0.7
): Path {

    data class Ant(
        val graph: Graph,
        val edgesToPheromone: MutableMap<Graph.Edge, Double>,
        val visitedVertices: MutableList<Graph.Vertex> = mutableListOf(),
        val visitedEdges: MutableSet<Graph.Edge> = mutableSetOf(),
        var isValid: Boolean = true
    ) {

        fun clear() {
            visitedEdges.clear()
            visitedVertices.clear()
            isValid = true
        }

        fun chooseNextVertex(current: Graph.Vertex): Graph.Vertex? {
            visitedVertices.add(current)
            return graph.getNeighbors(current)
                .filter { it !in visitedVertices }
                .maxBy {
                    calculateProbability(current, it)
                }
        }

        private fun calculateProbability(current: Graph.Vertex, next: Graph.Vertex): Double {
            var sum = 0.0
            for (neighbor in graph.getNeighbors(current)) {
                if (neighbor !in visitedVertices) {
                    val edge = graph.getConnection(current, neighbor)
                        ?: continue
                    sum += edgesToPheromone.getValue(edge).pow(alpha) *
                            (1.0 / edge.weight).pow(beta)
                }
            }
            val edge = graph.getConnection(current, next)
                ?: return 0.0
            val currentCoefficient = ((edgesToPheromone.getValue(edge)).pow(alpha) *
                    (1.0 / edge.weight).pow(beta))
            sum += currentCoefficient
            return currentCoefficient / sum
        }

        fun updateEdgesPheromone() {
            val len = visitedEdges.sumBy { it.weight }
            for (edge in visitedEdges) {
                val previousPheromone = edgesToPheromone.getValue(edge)
                edgesToPheromone[edge] = previousPheromone + 1.0 / len
            }
        }

        fun getPath(): Path? {
            var result = Path(visitedVertices.first())
            for (vertex in visitedVertices.minus(visitedVertices.first())) {
                if (graph.getConnection(result.vertices.last(), vertex) == null)
                    return null
                result = Path(result, graph, vertex)
            }
            return Path(result, graph, visitedVertices.first())
        }
    }

    val startVertex = vertices.first()
    val edgesToPheromone = mutableMapOf<Graph.Edge, Double>()
    edges.forEach { edgesToPheromone[it] = startedPheromone }
    val ants = List(antNumber) { Ant(this, edgesToPheromone) }
    var bestPath: Path? = null

    for (i in 0 until iterationNumber) {
        for (ant in ants) {
            var current = startVertex
            while (ant.visitedVertices.size != vertices.size - 1) {
                val next = ant.chooseNextVertex(current)
                if (next == null) {
                    ant.isValid = false
                    break
                }
                this.getConnection(current, next)?.let { ant.visitedEdges.add(it) }
                current = next
            }
            ant.visitedVertices.add(current)
            val finalEdge = this.getConnection(startVertex, current)
            if (finalEdge == null)
                ant.isValid = false
            else
                ant.visitedEdges.add(finalEdge)
        }
        val minPath = ants.minBy { it.getPath()?.length ?: Int.MAX_VALUE }?.getPath()
        if (bestPath == null || minPath?.length ?: Int.MAX_VALUE < bestPath.length) {
            bestPath = minPath
        }
        edgesToPheromone.mapValues { it.value * ro }
        ants.filter { it.isValid }.forEach { it.updateEdgesPheromone() }
        ants.forEach { it.clear() }
    }
    println(bestPath)
    return bestPath!!
}