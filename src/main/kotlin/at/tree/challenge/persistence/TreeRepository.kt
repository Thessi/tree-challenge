package at.tree.challenge.persistence

import at.tree.challenge.exception.InternalErrorException
import at.tree.challenge.service.model.Edge
import org.jooq.exception.DataAccessException
import org.jooq.generated.tables.references.EDGE
import org.jooq.impl.DSL.*
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.SQLDataType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class TreeRepository @Autowired constructor(private val dsl: DefaultDSLContext) {
    fun getTree(edgeId: Long, depth: Long): List<Edge> {
        val joinTableName = "recursive_edges"
        val joinTable = name(joinTableName)
        val depthColumnName = "depth"
        val depthColumn = field(name(depthColumnName), SQLDataType.BIGINT)
        val fromIdFieldName = "from_id"
        val toIdFieldName = "to_id"

        val baseQuery = dsl.select(EDGE.FROM_ID, EDGE.TO_ID, inline(1L).`as`(depthColumnName))
            .from(EDGE)
            .where(EDGE.FROM_ID.eq(edgeId))

        val recursiveQuery = dsl.select(EDGE.FROM_ID, EDGE.TO_ID, depthColumn.plus(1))
            .from(EDGE)
            .join(joinTable)
            .on(field(name(joinTableName, toIdFieldName), SQLDataType.BIGINT).eq(EDGE.FROM_ID))
            .where(depthColumn.lessThan(depth - 1))

        val cte = name(joinTableName)
            .fields(fromIdFieldName, toIdFieldName, depthColumnName)
            .`as`(baseQuery.unionAll(recursiveQuery))

        return try {
            dsl.withRecursive(cte)
                .selectDistinct(field(name(fromIdFieldName)), field(name(toIdFieldName)))
                .from(joinTable)
                .orderBy(field(name(fromIdFieldName)))
                .fetch()
                .map { Edge(it.get(EDGE.FROM_ID)!!, it.get(EDGE.TO_ID)!!) }
        } catch (e: DataAccessException) {
            throw InternalErrorException()
        }
    }

    fun createEdge(edge: Edge) {
        val query = dsl.insertInto(EDGE)
            .set(EDGE.FROM_ID, edge.fromId)
            .set(EDGE.TO_ID, edge.toId)

        try {
            query.execute()
        } catch (e: DataAccessException) {
            throw InternalErrorException()
        }
    }

    fun deleteEdge(edge: Edge) {
        val query = dsl.deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(edge.fromId))
            .and(EDGE.TO_ID.eq(edge.toId))

        try {
            query.execute()
        } catch (e: DataAccessException) {
            throw InternalErrorException()
        }
    }

    fun getEdge(edge: Edge): Edge? {
        val result = try {
            dsl.selectFrom(EDGE)
                .where(EDGE.FROM_ID.eq(edge.fromId))
                .and(EDGE.TO_ID.eq(edge.toId))
                .fetchAny()
        } catch (e: DataAccessException) {
            throw InternalErrorException()
        }

        if (result == null) {
            return null
        }

        return Edge(result.get(EDGE.FROM_ID)!!, result.get(EDGE.TO_ID)!!)
    }

    fun getEdgesByFromId(fromId: Long): List<Edge> {
        val result = try {
            dsl.selectFrom(EDGE)
                .where(EDGE.FROM_ID.eq(fromId))
                .fetch()
                .map { Edge(it.get(EDGE.FROM_ID)!!, it.get(EDGE.TO_ID)!!) }
        } catch (e: DataAccessException) {
            throw InternalErrorException()
        }

        return result
    }
}
