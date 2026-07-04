package com.example.watertracker.ui.routes

import kotlinx.serialization.Serializable

// =========================================================================
// ნავიგაციის მარშრუტები (Routes / Destinations)
// =========================================================================
//
// 💡 რატომ @Serializable object/data object, არა string?
// ძველად მარშრუტებს ვწერდით როგორც ტექსტს (მაგ: "home"), რაც typo-ს
// შემთხვევაში კომპილაციისას ვერაფერს შეამჩნევდა — შეცდომა მხოლოდ გაშვებისას
// გამოჩნდებოდა. @Serializable ობიექტებით ეს მარშრუტები თავად ტიპებია:
// კომპილატორი აკონტროლებს რომ `Screen.Home`-ს ვერსად ავურევთ `Screen.History`-ს.
//
// 💡 რატომ სამივე Screen-ს არ სჭირდება არგუმენტი?
// Home/History/Settings დამოუკიდებელი ეკრანებია, არცერთს არ სჭირდება გარედან
// მიღებული მონაცემი (მაგ. id ან username) — ამიტომ ყველა უბრალო `data object`-ია.
// =========================================================================

@Serializable
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object History : Screen

    @Serializable
    data object Settings : Screen
}
