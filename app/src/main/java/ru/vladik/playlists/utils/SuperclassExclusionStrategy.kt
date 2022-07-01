package ru.vladik.playlists.utils

import com.google.gson.FieldAttributes

import com.google.gson.ExclusionStrategy
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Modifier


object SuperclassExclusionStrategy : ExclusionStrategy {
    override fun shouldSkipClass(arg0: Class<*>?): Boolean {
        return false
    }

    override fun shouldSkipField(fieldAttributes: FieldAttributes): Boolean {
        if (fieldAttributes.hasModifier(Modifier.STRICT)) return true else return false
    }

    private fun isFieldInSuperclass(subclass: Class<*>, fieldName: String): Boolean {
        var superclass = subclass.superclass
        var field: Field?
        while (superclass != null) {
            field = getField(superclass, fieldName)
            if (field != null) return true
            superclass = superclass.superclass
        }
        return false
    }

    private fun getField(theClass: Class<*>, fieldName: String): Field? {
        return try {
            theClass.getDeclaredField(fieldName)
        } catch (e: Exception) {
            null
        }
    }
}