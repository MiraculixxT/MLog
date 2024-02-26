package de.miraculixx.mlog.global

import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import java.util.concurrent.CompletableFuture

class StringSuggestionProvider<T>(private val set: Set<String>) : SuggestionProvider<T> {
    override fun getSuggestions(context: CommandContext<T>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        set.forEach { builder.suggest(it) }
        return builder.buildFuture()
    }
}