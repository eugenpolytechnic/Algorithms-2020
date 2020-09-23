@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson8

import lesson6.Graph
import lesson6.Path
import lesson7.knapsack.Fill
import lesson7.knapsack.Item
import java.lang.RuntimeException
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
//не проверяйте пока, пожалуйста, оно все работает,
//но я буду рефакторить
fun Graph.findVoyagingPathHeuristics(antNumber: Int, iterationNumber: Int): Path {
    val startVertex = vertices.first()
    val startedPheromone = 1.0 //?
    val edgesToPheromone = mutableMapOf<Graph.Edge, Double>()
    edges.forEach { edgesToPheromone[it] = startedPheromone }
    val ants = List(antNumber) { Ant() }

    for (i in 0 until iterationNumber) {

        for (ant in ants) {
            var current = startVertex
            while (ant.visitedVertices.size != vertices.size - 1) {
                val next = ant.chooseNextVertex(current, edgesToPheromone, this)
                if (next == null) {
                    ant.isValid = false
                    break
                }
                ant.visitedVertices.add(current)
                ant.visitedEdges.add( this.getConnection(current, next)!! )
                current = next
            }
            ant.visitedVertices.add(current)
            val finalEdge = this.getConnection(startVertex, current)
            if (finalEdge == null)
                ant.isValid = false
            else
                ant.visitedEdges.add(finalEdge)
        }

        ants.filter { it.isValid }.forEach { it.updateEdgesPheromone(edgesToPheromone) }
    }

    var result = Path(startVertex)
    val visited = mutableSetOf<Graph.Vertex>()
    val ant = Ant()
    var current = startVertex
    while (ant.visitedVertices.size != vertices.size - 1) {
        val next = ant.chooseNextVertex(current, edgesToPheromone, this)
        if (next == null) {
            ant.isValid = false
            break
        }
        ant.visitedVertices.add(current)
        ant.visitedEdges.add( this.getConnection(current, next)!! )
        current = next
        result = Path(result, this, current)
    }

    ant.visitedVertices.add(current)
    val finalEdge = this.getConnection(startVertex, current)
    if (finalEdge == null)
        ant.isValid = false
    else
        ant.visitedEdges.add(finalEdge)
    println(result)
    return Path(result, this, startVertex)
}

data class Ant(
    val visitedVertices: MutableSet<Graph.Vertex> = mutableSetOf(),
    val visitedEdges: MutableList<Graph.Edge> = mutableListOf(),
    var isValid: Boolean = true
) {
    fun chooseNextVertex(
        current: Graph.Vertex,
        edgesToPheromone: Map<Graph.Edge, Double>,
        graph: Graph
    ): Graph.Vertex? =
        graph.getNeighbors(current).filter { it !in visitedVertices }.maxBy {
            calculateProbability(current, it, edgesToPheromone, graph)
        }

    private fun calculateProbability(
        current: Graph.Vertex,
        next: Graph.Vertex,
        edgesToPheromone: Map<Graph.Edge, Double>,
        graph: Graph
    ): Double {
        val alpha = 1 //?
        val beta = 1 //?
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

    fun updateEdgesPheromone(edgesToPheromone: MutableMap<Graph.Edge, Double>) {
        val ro = 0.91 //?
        val len = visitedEdges.sumBy { it.weight }
        for (edge in visitedEdges) {
            val previousPheromone = edgesToPheromone.getValue(edge)
            edgesToPheromone[edge] = ro * previousPheromone + 1.0 / len
        }
    }
}
