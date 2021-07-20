package com.example.demo.gql.scalars

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsRuntimeWiring
import com.netflix.graphql.dgs.DgsScalar
import graphql.language.StringValue
import graphql.scalars.ExtendedScalars
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.idl.RuntimeWiring
import java.util.*


@DgsComponent
class DateTimeScalarRegistration {

    @DgsRuntimeWiring
    fun addScalar(builder: RuntimeWiring.Builder): RuntimeWiring.Builder {
        return builder.scalar(ExtendedScalars.DateTime)
    }
}

@DgsScalar(name = "UUID")
class UUIDScalar : Coercing<UUID, String> {
    @Throws(CoercingSerializeException::class)
    override fun serialize(dataFetcherResult: Any): String {
        return when (dataFetcherResult) {
            is UUID -> dataFetcherResult.toString()
            else -> throw CoercingSerializeException("Not a valid uuid")
        }
    }

    @Throws(CoercingParseValueException::class)
    override fun parseValue(input: Any): UUID {
        return UUID.fromString(input.toString())
    }

    @Throws(CoercingParseLiteralException::class)
    override fun parseLiteral(input: Any): UUID {
        if (input is StringValue) {
            val a = (input as StringValue).getValue()
            return UUID.fromString(a)
        }
        throw CoercingParseLiteralException("Value is not a valid uuid")
    }
}

