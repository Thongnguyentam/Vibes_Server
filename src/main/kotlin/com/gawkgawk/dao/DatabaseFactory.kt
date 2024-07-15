package com.gawkgawk.dao

import com.gawkgawk.dao.follows.FollowsTable
import com.gawkgawk.dao.post.PostTable
import com.gawkgawk.dao.post_comments.PostCommentsTable
import com.gawkgawk.dao.post_likes.PostLikesTable
import com.gawkgawk.dao.user.UserRow
import com.gawkgawk.dao.user.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Object responsible for initializing and managing the database connection.
 *  It includes methods for initializing the database schema and
 *  for executing database queries within suspended transactions.
 */
object DatabaseFactory {

    /**
     * Initializes the database connection and sets up the schema.
     */
    fun init(){
        Database.connect(createHikariDataSource())
        transaction {
            SchemaUtils.create(UserTable, FollowsTable, PostTable, PostLikesTable, PostCommentsTable)
        }
    }

    /**
     * Creates and configures the HikariCP data source.
     * @return The configured HikariDataSource.
     */
    private fun createHikariDataSource(): HikariDataSource{
        val driverClass = "org.postgresql.Driver"
        val jdbcUrl = "jdbc:postgresql://localhost:5432/gawkgawk"

        val hikariConfig = HikariConfig().apply {
            driverClassName = driverClass
            setJdbcUrl(jdbcUrl)
            username = "postgres"
            password = "Meomeomeo123@"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }

    /**
     * Executes a database query in a suspended transaction.
     * @param block The suspend function to execute.
     * @return The result of the query.
     */
    // execute on all database queries
    suspend fun <T> dbQuery(block: suspend () -> T) = // take suspend function and return type T
        newSuspendedTransaction( Dispatchers.IO) { block() } //execute block
    // start a new suspended transaction
}