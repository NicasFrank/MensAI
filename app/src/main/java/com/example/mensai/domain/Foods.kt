package com.example.mensai.domain

enum class Foods {
    UNKNOWN,
    PASTATELLER,
    ALASKA_SEELACHS_EIHUELLE,
    SCHWARZBIERPFANNE,
    SEELACHS_PANADE,
    ELSAESSER_HAENCHEN,
    MANGO_SPARGEL_PFANNE,
    ALASKA_SEELACHS_HOLLANDAISE,
    FLADENBROT_KEBAP,
    FRUEHLINGSROLLE,
    PUTENGYROS,
}

fun Foods.getPrice() : Float{
    return when(this){
        Foods.UNKNOWN -> 0.0f
        Foods.PASTATELLER -> 2.10f
        Foods.ALASKA_SEELACHS_EIHUELLE -> 4.10f
        Foods.SCHWARZBIERPFANNE -> 4.10f
        Foods.SEELACHS_PANADE -> 4.10f
        Foods.ELSAESSER_HAENCHEN -> 3.70f
        Foods.MANGO_SPARGEL_PFANNE -> 3.50f
        Foods.ALASKA_SEELACHS_HOLLANDAISE -> 4.10f
        Foods.FLADENBROT_KEBAP -> 4.50f
        Foods.FRUEHLINGSROLLE -> 2.40f
        Foods.PUTENGYROS -> 2.65f
    }
}

fun Foods.getFullName(): String{
    return when(this){
        Foods.UNKNOWN -> ""
        Foods.PASTATELLER -> "Pastateller"
        Foods.ALASKA_SEELACHS_EIHUELLE -> "Alaska-Seelachsfilet in Eihülle"
        Foods.SCHWARZBIERPFANNE -> "Schwarzbierpfanne vom Rind"
        Foods.SEELACHS_PANADE -> "Seelachfilet in Kartoffelpanade"
        Foods.ELSAESSER_HAENCHEN -> "Elsässer Hähnchen mit Champignons, Knoblauch und Kräutern"
        Foods.MANGO_SPARGEL_PFANNE -> "Mango-Spargel-Pfanne"
        Foods.ALASKA_SEELACHS_HOLLANDAISE -> "Alaska-Seelachsfilet in Eihülle mit Sauce Hollandaise"
        Foods.FLADENBROT_KEBAP -> "Fladenbrot mit Seitan-Kebap"
        Foods.FRUEHLINGSROLLE -> "Frühlingsrolle mit Gemüsefüllung"
        Foods.PUTENGYROS -> "Putengyros"
    }
}