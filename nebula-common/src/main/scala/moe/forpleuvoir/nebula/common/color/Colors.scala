package moe.forpleuvoir.nebula.common.color

object Colors {

  private def color(
    red: Int,
    green: Int,
    blue: Int,
  ): Color = Color.fromARGB(0xFF000000 | (red << 16) | (green << 8) | blue)

  lazy val BLACK: Color = color(0, 0, 0)

  lazy val NIGHT: Color = color(12, 9, 10)

  lazy val CHARCOAL: Color = color(52, 40, 44)

  lazy val OIL: Color = color(59, 49, 49)

  lazy val DARK_GRAY: Color = color(58, 59, 60)

  lazy val LIGHT_BLACK: Color = color(69, 69, 69)

  lazy val BLACK_CAT: Color = color(65, 56, 57)

  lazy val IRIDIUM: Color = color(61, 60, 58)

  lazy val BLACK_EEL: Color = color(70, 62, 63)

  lazy val BLACK_COW: Color = color(76, 70, 70)

  lazy val GRAY_WOLF: Color = color(80, 74, 75)

  lazy val VAMPIRE_GRAY: Color = color(86, 80, 81)

  lazy val IRON_GRAY: Color = color(82, 89, 93)

  lazy val GRAY_DOLPHIN: Color = color(92, 88, 88)

  lazy val CARBON_GRAY: Color = color(98, 93, 93)

  lazy val ASH_GRAY: Color = color(102, 99, 98)

  lazy val DIMGRAY: Color = color(105, 105, 105)

  lazy val NARDO_GRAY: Color = color(104, 106, 108)

  lazy val CLOUDY_GRAY: Color = color(109, 105, 104)

  lazy val SMOKEY_GRAY: Color = color(114, 110, 109)

  lazy val ALIEN_GRAY: Color = color(115, 111, 110)

  lazy val SONIC_SILVER: Color = color(117, 117, 117)

  lazy val PLATINUM_GRAY: Color = color(121, 121, 121)

  lazy val GRANITE: Color = color(131, 126, 124)

  lazy val GRAY: Color = color(128, 128, 128)

  lazy val BATTLESHIP_GRAY: Color = color(132, 132, 130)

  lazy val GUNMETAL_GRAY: Color = color(141, 145, 141)

  lazy val DARKGRAY: Color = color(169, 169, 169)

  lazy val GRAY_CLOUD: Color = color(182, 182, 180)

  lazy val SILVER: Color = color(192, 192, 192)

  lazy val PALE_SILVER: Color = color(201, 192, 187)

  lazy val GRAY_GOOSE: Color = color(209, 208, 206)

  lazy val PLATINUM_SILVER: Color = color(206, 206, 206)

  lazy val LIGHTGRAY: Color = color(211, 211, 211)

  lazy val SILVER_WHITE: Color = color(218, 219, 221)

  lazy val GAINSBORO: Color = color(220, 220, 220)

  lazy val PLATINUM: Color = color(229, 228, 226)

  lazy val METALLIC_SILVER: Color = color(188, 198, 204)

  lazy val BLUE_GRAY: Color = color(152, 175, 199)

  lazy val ROMAN_SILVER: Color = color(131, 137, 150)

  lazy val LIGHTSLATEGRAY: Color = color(119, 136, 153)

  lazy val SLATEGRAY: Color = color(112, 128, 144)

  lazy val RAT_GRAY: Color = color(109, 123, 141)

  lazy val SLATE_GRANITE_GRAY: Color = color(101, 115, 131)

  lazy val JET_GRAY: Color = color(97, 109, 126)

  lazy val MIST_BLUE: Color = color(100, 109, 126)

  lazy val MARBLE_BLUE: Color = color(86, 109, 126)

  lazy val SLATE_BLUE_GREY: Color = color(115, 124, 161)

  lazy val LIGHT_PURPLE_BLUE: Color = color(114, 143, 206)

  lazy val AZURE_BLUE: Color = color(72, 99, 160)

  lazy val BLUE_JAY: Color = color(43, 84, 126)

  lazy val CHARCOAL_BLUE: Color = color(54, 69, 79)

  lazy val DARK_BLUE_GREY: Color = color(41, 70, 91)

  lazy val DARK_SLATE: Color = color(43, 56, 86)

  lazy val DEEP_SEA_BLUE: Color = color(18, 52, 86)

  lazy val NIGHT_BLUE: Color = color(21, 27, 84)

  lazy val MIDNIGHTBLUE: Color = color(25, 25, 112)

  lazy val NAVY: Color = color(0, 0, 128)

  lazy val DENIM_DARK_BLUE: Color = color(21, 27, 141)

  lazy val DARKBLUE: Color = color(0, 0, 139)

  lazy val LAPIS_BLUE: Color = color(21, 49, 126)

  lazy val NEW_MIDNIGHT_BLUE: Color = color(0, 0, 160)

  lazy val EARTH_BLUE: Color = color(0, 0, 165)

  lazy val COBALT_BLUE: Color = color(0, 32, 194)

  lazy val MEDIUMBLUE: Color = color(0, 0, 205)

  lazy val BLUEBERRY_BLUE: Color = color(0, 65, 194)

  lazy val CANARY_BLUE: Color = color(41, 22, 245)

  lazy val BLUE: Color = color(0, 0, 255)

  lazy val SAMCO_BLUE: Color = color(0, 2, 255)

  lazy val BRIGHT_BLUE: Color = color(9, 9, 255)

  lazy val BLUE_ORCHID: Color = color(31, 69, 252)

  lazy val SAPPHIRE_BLUE: Color = color(37, 84, 199)

  lazy val BLUE_EYES: Color = color(21, 105, 199)

  lazy val BRIGHT_NAVY_BLUE: Color = color(25, 116, 210)

  lazy val BALLOON_BLUE: Color = color(43, 96, 222)

  lazy val ROYALBLUE: Color = color(65, 105, 225)

  lazy val OCEAN_BLUE: Color = color(43, 101, 236)

  lazy val BLUE_RIBBON: Color = color(48, 110, 255)

  lazy val BLUE_DRESS: Color = color(21, 125, 236)

  lazy val NEON_BLUE: Color = color(21, 137, 255)

  lazy val DODGERBLUE: Color = color(30, 144, 255)

  lazy val GLACIAL_BLUE_ICE: Color = color(54, 139, 193)

  lazy val STEELBLUE: Color = color(70, 130, 180)

  lazy val SILK_BLUE: Color = color(72, 138, 199)

  lazy val WINDOWS_BLUE: Color = color(53, 126, 199)

  lazy val BLUE_IVY: Color = color(48, 144, 199)

  lazy val BLUE_KOI: Color = color(101, 158, 199)

  lazy val COLUMBIA_BLUE: Color = color(135, 175, 199)

  lazy val BABY_BLUE: Color = color(149, 185, 199)

  lazy val CORNFLOWERBLUE: Color = color(100, 149, 237)

  lazy val SKY_BLUE_DRESS: Color = color(102, 152, 255)

  lazy val ICEBERG: Color = color(86, 165, 236)

  lazy val BUTTERFLY_BLUE: Color = color(56, 172, 236)

  lazy val DEEPSKYBLUE: Color = color(0, 191, 255)

  lazy val MIDDAY_BLUE: Color = color(59, 185, 255)

  lazy val CRYSTAL_BLUE: Color = color(92, 179, 255)

  lazy val DENIM_BLUE: Color = color(121, 186, 236)

  lazy val DAY_SKY_BLUE: Color = color(130, 202, 255)

  lazy val LIGHTSKYBLUE: Color = color(135, 206, 250)

  lazy val SKYBLUE: Color = color(135, 206, 235)

  lazy val JEANS_BLUE: Color = color(160, 207, 236)

  lazy val BLUE_ANGEL: Color = color(183, 206, 236)

  lazy val PASTEL_BLUE: Color = color(180, 207, 236)

  lazy val LIGHT_DAY_BLUE: Color = color(173, 223, 255)

  lazy val SEA_BLUE: Color = color(194, 223, 255)

  lazy val HEAVENLY_BLUE: Color = color(198, 222, 255)

  lazy val ROBIN_EGG_BLUE: Color = color(189, 237, 255)

  lazy val POWDERBLUE: Color = color(176, 224, 230)

  lazy val CORAL_BLUE: Color = color(175, 220, 236)

  lazy val LIGHTBLUE: Color = color(173, 216, 230)

  lazy val LIGHTSTEELBLUE: Color = color(176, 207, 222)

  lazy val GULF_BLUE: Color = color(201, 223, 236)

  lazy val PASTEL_LIGHT_BLUE: Color = color(213, 214, 234)

  lazy val LAVENDER_BLUE: Color = color(227, 228, 250)

  lazy val WHITE_BLUE: Color = color(219, 233, 250)

  lazy val LAVENDER: Color = color(230, 230, 250)

  lazy val WATER: Color = color(235, 244, 250)

  lazy val ALICEBLUE: Color = color(240, 248, 255)

  lazy val GHOSTWHITE: Color = color(248, 248, 255)

  lazy val AZURE: Color = color(240, 255, 255)

  lazy val LIGHTCYAN: Color = color(224, 255, 255)

  lazy val LIGHT_SLATE: Color = color(204, 255, 255)

  lazy val ELECTRIC_BLUE: Color = color(154, 254, 255)

  lazy val TRON_BLUE: Color = color(125, 253, 254)

  lazy val BLUE_ZIRCON: Color = color(87, 254, 255)

  lazy val AQUA: Color = color(0, 255, 255)

  lazy val CYAN: Color = color(10, 255, 255)

  lazy val CELESTE: Color = color(80, 235, 236)

  lazy val BLUE_DIAMOND: Color = color(78, 226, 236)

  lazy val BRIGHT_TURQUOISE: Color = color(22, 226, 245)

  lazy val BLUE_LAGOON: Color = color(142, 235, 236)

  lazy val PALETURQUOISE: Color = color(175, 238, 238)

  lazy val PALE_BLUE_LILY: Color = color(207, 236, 236)

  lazy val LIGHT_TEAL: Color = color(179, 217, 217)

  lazy val TIFFANY_BLUE: Color = color(129, 216, 208)

  lazy val BLUE_HOSTA: Color = color(119, 191, 199)

  lazy val CYAN_OPAQUE: Color = color(146, 199, 199)

  lazy val NORTHERN_LIGHTS_BLUE: Color = color(120, 199, 199)

  lazy val BLUE_GREEN: Color = color(123, 204, 181)

  lazy val MEDIUMAQUAMARINE: Color = color(102, 205, 170)

  lazy val MAGIC_MINT: Color = color(170, 240, 209)

  lazy val LIGHT_AQUAMARINE: Color = color(147, 255, 232)

  lazy val AQUAMARINE: Color = color(127, 255, 212)

  lazy val BRIGHT_TEAL: Color = color(1, 249, 198)

  lazy val TURQUOISE: Color = color(64, 224, 208)

  lazy val MEDIUMTURQUOISE: Color = color(72, 209, 204)

  lazy val DEEP_TURQUOISE: Color = color(72, 204, 205)

  lazy val JELLYFISH: Color = color(70, 199, 199)

  lazy val BLUE_TURQUOISE: Color = color(67, 198, 219)

  lazy val DARKTURQUOISE: Color = color(0, 206, 209)

  lazy val MACAW_BLUE_GREEN: Color = color(67, 191, 199)

  lazy val LIGHTSEAGREEN: Color = color(32, 178, 170)

  lazy val SEAFOAM_GREEN: Color = color(62, 169, 159)

  lazy val CADETBLUE: Color = color(95, 158, 160)

  lazy val DEEP_SEA: Color = color(59, 156, 156)

  lazy val DARKCYAN: Color = color(0, 139, 139)

  lazy val TEAL_GREEN: Color = color(0, 130, 127)

  lazy val TEAL: Color = color(0, 128, 128)

  lazy val TEAL_BLUE: Color = color(0, 124, 128)

  lazy val MEDIUM_TEAL: Color = color(4, 95, 95)

  lazy val DARK_TEAL: Color = color(4, 93, 93)

  lazy val DEEP_TEAL: Color = color(3, 62, 62)

  lazy val DARKSLATEGRAY: Color = color(37, 56, 60)

  lazy val GUNMETAL: Color = color(44, 53, 57)

  lazy val BLUE_MOSS_GREEN: Color = color(60, 86, 91)

  lazy val BEETLE_GREEN: Color = color(76, 120, 126)

  lazy val GRAYISH_TURQUOISE: Color = color(94, 125, 126)

  lazy val GREENISH_BLUE: Color = color(48, 125, 126)

  lazy val AQUAMARINE_STONE: Color = color(52, 135, 129)

  lazy val SEA_TURTLE_GREEN: Color = color(67, 141, 128)

  lazy val DULL_SEA_GREEN: Color = color(78, 137, 117)

  lazy val DARK_GREEN_BLUE: Color = color(31, 99, 87)

  lazy val DEEP_SEA_GREEN: Color = color(48, 103, 84)

  lazy val BOTTLE_GREEN: Color = color(0, 106, 78)

  lazy val SEAGREEN: Color = color(46, 139, 87)

  lazy val ELF_GREEN: Color = color(27, 138, 107)

  lazy val DARK_MINT: Color = color(49, 144, 110)

  lazy val JADE: Color = color(0, 163, 108)

  lazy val EARTH_GREEN: Color = color(52, 165, 111)

  lazy val CHROME_GREEN: Color = color(26, 162, 96)

  lazy val EMERALD: Color = color(80, 200, 120)

  lazy val MINT: Color = color(62, 180, 137)

  lazy val MEDIUMSEAGREEN: Color = color(60, 179, 113)

  lazy val METALLIC_GREEN: Color = color(124, 157, 142)

  lazy val CAMOUFLAGE_GREEN: Color = color(120, 134, 107)

  lazy val SAGE_GREEN: Color = color(132, 139, 121)

  lazy val HAZEL_GREEN: Color = color(97, 124, 88)

  lazy val VENOM_GREEN: Color = color(114, 140, 0)

  lazy val OLIVEDRAB: Color = color(107, 142, 35)

  lazy val OLIVE: Color = color(128, 128, 0)

  lazy val DARKOLIVEGREEN: Color = color(85, 107, 47)

  lazy val MILITARY_GREEN: Color = color(78, 91, 49)

  lazy val GREEN_LEAVES: Color = color(58, 95, 11)

  lazy val ARMY_GREEN: Color = color(75, 83, 32)

  lazy val FERN_GREEN: Color = color(102, 124, 38)

  lazy val FALL_FOREST_GREEN: Color = color(78, 146, 88)

  lazy val IRISH_GREEN: Color = color(8, 160, 75)

  lazy val PINE_GREEN: Color = color(56, 124, 68)

  lazy val MEDIUM_FOREST_GREEN: Color = color(52, 114, 53)

  lazy val JUNGLE_GREEN: Color = color(52, 124, 44)

  lazy val CACTUS_GREEN: Color = color(34, 116, 66)

  lazy val FORESTGREEN: Color = color(34, 139, 34)

  lazy val GREEN: Color = color(0, 128, 0)

  lazy val DARKGREEN: Color = color(0, 100, 0)

  lazy val DEEP_GREEN: Color = color(5, 102, 8)

  lazy val DEEP_EMERALD_GREEN: Color = color(4, 99, 7)

  lazy val HUNTER_GREEN: Color = color(53, 94, 59)

  lazy val DARK_FOREST_GREEN: Color = color(37, 65, 23)

  lazy val LOTUS_GREEN: Color = color(0, 66, 37)

  lazy val SEAWEED_GREEN: Color = color(67, 124, 23)

  lazy val SHAMROCK_GREEN: Color = color(52, 124, 23)

  lazy val GREEN_ONION: Color = color(106, 161, 33)

  lazy val MOSS_GREEN: Color = color(138, 154, 91)

  lazy val GRASS_GREEN: Color = color(63, 155, 11)

  lazy val GREEN_PEPPER: Color = color(74, 160, 44)

  lazy val DARK_LIME_GREEN: Color = color(65, 163, 23)

  lazy val PARROT_GREEN: Color = color(18, 173, 43)

  lazy val CLOVER_GREEN: Color = color(62, 160, 85)

  lazy val DINOSAUR_GREEN: Color = color(115, 161, 108)

  lazy val GREEN_SNAKE: Color = color(108, 187, 60)

  lazy val ALIEN_GREEN: Color = color(108, 196, 23)

  lazy val GREEN_APPLE: Color = color(76, 196, 23)

  lazy val LIMEGREEN: Color = color(50, 205, 50)

  lazy val PEA_GREEN: Color = color(82, 208, 23)

  lazy val KELLY_GREEN: Color = color(76, 197, 82)

  lazy val ZOMBIE_GREEN: Color = color(84, 197, 113)

  lazy val GREEN_PEAS: Color = color(137, 195, 92)

  lazy val DOLLAR_BILL_GREEN: Color = color(133, 187, 101)

  lazy val FROG_GREEN: Color = color(153, 198, 142)

  lazy val TURQUOISE_GREEN: Color = color(160, 214, 180)

  lazy val DARKSEAGREEN: Color = color(143, 188, 143)

  lazy val BASIL_GREEN: Color = color(130, 159, 130)

  lazy val GRAY_GREEN: Color = color(162, 173, 156)

  lazy val IGUANA_GREEN: Color = color(156, 176, 113)

  lazy val CITRON_GREEN: Color = color(143, 179, 29)

  lazy val ACID_GREEN: Color = color(176, 191, 26)

  lazy val AVOCADO_GREEN: Color = color(178, 194, 72)

  lazy val PISTACHIO_GREEN: Color = color(157, 194, 9)

  lazy val SALAD_GREEN: Color = color(161, 201, 53)

  lazy val YELLOWGREEN: Color = color(154, 205, 50)

  lazy val PASTEL_GREEN: Color = color(119, 221, 119)

  lazy val HUMMINGBIRD_GREEN: Color = color(127, 232, 23)

  lazy val NEBULA_GREEN: Color = color(89, 232, 23)

  lazy val STOPLIGHT_GO_GREEN: Color = color(87, 233, 100)

  lazy val NEON_GREEN: Color = color(22, 245, 41)

  lazy val JADE_GREEN: Color = color(94, 251, 110)

  lazy val LIME_MINT_GREEN: Color = color(54, 245, 127)

  lazy val SPRINGGREEN: Color = color(0, 255, 127)

  lazy val MEDIUMSPRINGGREEN: Color = color(0, 250, 154)

  lazy val EMERALD_GREEN: Color = color(95, 251, 23)

  lazy val LIME: Color = color(0, 255, 0)

  lazy val LAWNGREEN: Color = color(124, 252, 0)

  lazy val BRIGHT_GREEN: Color = color(102, 255, 0)

  lazy val CHARTREUSE: Color = color(127, 255, 0)

  lazy val YELLOW_LAWN_GREEN: Color = color(135, 247, 23)

  lazy val ALOE_VERA_GREEN: Color = color(152, 245, 22)

  lazy val DULL_GREEN_YELLOW: Color = color(177, 251, 23)

  lazy val LEMON_GREEN: Color = color(173, 248, 2)

  lazy val GREENYELLOW: Color = color(173, 255, 47)

  lazy val CHAMELEON_GREEN: Color = color(189, 245, 22)

  lazy val NEON_YELLOW_GREEN: Color = color(218, 238, 1)

  lazy val YELLOW_GREEN_GROSBEAK: Color = color(226, 245, 22)

  lazy val TEA_GREEN: Color = color(204, 251, 93)

  lazy val SLIME_GREEN: Color = color(188, 233, 84)

  lazy val ALGAE_GREEN: Color = color(100, 233, 134)

  lazy val LIGHTGREEN: Color = color(144, 238, 144)

  lazy val DRAGON_GREEN: Color = color(106, 251, 146)

  lazy val PALEGREEN: Color = color(152, 251, 152)

  lazy val MINT_GREEN: Color = color(152, 255, 152)

  lazy val GREEN_THUMB: Color = color(181, 234, 170)

  lazy val ORGANIC_BROWN: Color = color(227, 249, 166)

  lazy val LIGHT_JADE: Color = color(195, 253, 184)

  lazy val LIGHT_MINT_GREEN: Color = color(194, 229, 211)

  lazy val LIGHT_ROSE_GREEN: Color = color(219, 249, 219)

  lazy val CHROME_WHITE: Color = color(232, 241, 212)

  lazy val HONEYDEW: Color = color(240, 255, 240)

  lazy val MINTCREAM: Color = color(245, 255, 250)

  lazy val LEMONCHIFFON: Color = color(255, 250, 205)

  lazy val PARCHMENT: Color = color(255, 255, 194)

  lazy val CREAM: Color = color(255, 255, 204)

  lazy val CREAM_WHITE: Color = color(255, 253, 208)

  lazy val LIGHTGOLDENRODYELLOW: Color = color(250, 250, 210)

  lazy val LIGHTYELLOW: Color = color(255, 255, 224)

  lazy val BEIGE: Color = color(245, 245, 220)

  lazy val CORNSILK: Color = color(255, 248, 220)

  lazy val BLONDE: Color = color(251, 246, 217)

  lazy val CHAMPAGNE: Color = color(247, 231, 206)

  lazy val ANTIQUEWHITE: Color = color(250, 235, 215)

  lazy val PAPAYAWHIP: Color = color(255, 239, 213)

  lazy val BLANCHEDALMOND: Color = color(255, 235, 205)

  lazy val BISQUE: Color = color(255, 228, 196)

  lazy val WHEAT: Color = color(245, 222, 179)

  lazy val MOCCASIN: Color = color(255, 228, 181)

  lazy val PEACH: Color = color(255, 229, 180)

  lazy val LIGHT_ORANGE: Color = color(254, 216, 177)

  lazy val PEACHPUFF: Color = color(255, 218, 185)

  lazy val CORAL_PEACH: Color = color(251, 213, 171)

  lazy val NAVAJOWHITE: Color = color(255, 222, 173)

  lazy val GOLDEN_BLONDE: Color = color(251, 231, 161)

  lazy val GOLDEN_SILK: Color = color(243, 227, 195)

  lazy val DARK_BLONDE: Color = color(240, 226, 182)

  lazy val LIGHT_GOLD: Color = color(241, 229, 172)

  lazy val VANILLA: Color = color(243, 229, 171)

  lazy val TAN_BROWN: Color = color(236, 229, 182)

  lazy val DIRTY_WHITE: Color = color(232, 228, 201)

  lazy val PALEGOLDENROD: Color = color(238, 232, 170)

  lazy val KHAKI: Color = color(240, 230, 140)

  lazy val CARDBOARD_BROWN: Color = color(237, 218, 116)

  lazy val HARVEST_GOLD: Color = color(237, 226, 117)

  lazy val SUN_YELLOW: Color = color(255, 232, 124)

  lazy val CORN_YELLOW: Color = color(255, 243, 128)

  lazy val PASTEL_YELLOW: Color = color(250, 248, 132)

  lazy val NEON_YELLOW: Color = color(255, 255, 51)

  lazy val YELLOW: Color = color(255, 255, 0)

  lazy val CANARY_YELLOW: Color = color(255, 239, 0)

  lazy val BANANA_YELLOW: Color = color(245, 226, 22)

  lazy val MUSTARD_YELLOW: Color = color(255, 219, 88)

  lazy val GOLDEN_YELLOW: Color = color(255, 223, 0)

  lazy val BOLD_YELLOW: Color = color(249, 219, 36)

  lazy val RUBBER_DUCKY_YELLOW: Color = color(255, 216, 1)

  lazy val GOLD: Color = color(255, 215, 0)

  lazy val BRIGHT_GOLD: Color = color(253, 208, 23)

  lazy val CHROME_GOLD: Color = color(255, 206, 68)

  lazy val GOLDEN_BROWN: Color = color(234, 193, 23)

  lazy val DEEP_YELLOW: Color = color(246, 190, 0)

  lazy val MACARONI_AND_CHEESE: Color = color(242, 187, 102)

  lazy val SAFFRON: Color = color(251, 185, 23)

  lazy val NEON_GOLD: Color = color(253, 189, 1)

  lazy val BEER: Color = color(251, 177, 23)

  lazy val ORANGE_YELLOW: Color = color(255, 174, 66)

  lazy val CANTALOUPE: Color = color(255, 166, 47)

  lazy val CHEESE_ORANGE: Color = color(255, 166, 0)

  lazy val ORANGE: Color = color(255, 165, 0)

  lazy val BROWN_SAND: Color = color(238, 154, 77)

  lazy val SANDYBROWN: Color = color(244, 164, 96)

  lazy val BROWN_SUGAR: Color = color(226, 167, 111)

  lazy val CAMEL_BROWN: Color = color(193, 154, 107)

  lazy val DEER_BROWN: Color = color(230, 191, 131)

  lazy val BURLYWOOD: Color = color(222, 184, 135)

  lazy val TAN: Color = color(210, 180, 140)

  lazy val LIGHT_FRENCH_BEIGE: Color = color(200, 173, 127)

  lazy val SAND: Color = color(194, 178, 128)

  lazy val SAGE: Color = color(188, 184, 138)

  lazy val FALL_LEAF_BROWN: Color = color(200, 181, 96)

  lazy val GINGER_BROWN: Color = color(201, 190, 98)

  lazy val BRONZE_GOLD: Color = color(201, 174, 93)

  lazy val DARKKHAKI: Color = color(189, 183, 107)

  lazy val OLIVE_GREEN: Color = color(186, 184, 108)

  lazy val BRASS: Color = color(181, 166, 66)

  lazy val COOKIE_BROWN: Color = color(199, 163, 23)

  lazy val METALLIC_GOLD: Color = color(212, 175, 55)

  lazy val BEE_YELLOW: Color = color(233, 171, 23)

  lazy val SCHOOL_BUS_YELLOW: Color = color(232, 163, 23)

  lazy val GOLDENROD: Color = color(218, 165, 32)

  lazy val ORANGE_GOLD: Color = color(212, 160, 23)

  lazy val CARAMEL: Color = color(198, 142, 23)

  lazy val DARKGOLDENROD: Color = color(184, 134, 11)

  lazy val CINNAMON: Color = color(197, 137, 23)

  lazy val PERU: Color = color(205, 133, 63)

  lazy val BRONZE: Color = color(205, 127, 50)

  lazy val TIGER_ORANGE: Color = color(200, 129, 65)

  lazy val COPPER: Color = color(184, 115, 51)

  lazy val DARK_GOLD: Color = color(170, 108, 57)

  lazy val METALLIC_BRONZE: Color = color(169, 113, 66)

  lazy val DARK_ALMOND: Color = color(171, 120, 78)

  lazy val WOOD: Color = color(150, 111, 51)

  lazy val OAK_BROWN: Color = color(128, 101, 23)

  lazy val ANTIQUE_BRONZE: Color = color(102, 93, 30)

  lazy val HAZEL: Color = color(142, 118, 24)

  lazy val DARK_YELLOW: Color = color(139, 128, 0)

  lazy val DARK_MOCCASIN: Color = color(130, 120, 57)

  lazy val KHAKI_GREEN: Color = color(138, 134, 93)

  lazy val MILLENNIUM_JADE: Color = color(147, 145, 124)

  lazy val DARK_BEIGE: Color = color(159, 140, 118)

  lazy val BULLET_SHELL: Color = color(175, 155, 96)

  lazy val ARMY_BROWN: Color = color(130, 123, 96)

  lazy val SANDSTONE: Color = color(120, 109, 95)

  lazy val TAUPE: Color = color(72, 60, 50)

  lazy val MOCHA: Color = color(73, 61, 38)

  lazy val MILK_CHOCOLATE: Color = color(81, 59, 28)

  lazy val GRAY_BROWN: Color = color(61, 54, 53)

  lazy val DARK_COFFEE: Color = color(59, 47, 47)

  lazy val OLD_BURGUNDY: Color = color(67, 48, 46)

  lazy val WESTERN_CHARCOAL: Color = color(73, 65, 63)

  lazy val BAKERS_BROWN: Color = color(92, 51, 23)

  lazy val DARK_BROWN: Color = color(101, 67, 33)

  lazy val SEPIA_BROWN: Color = color(112, 66, 20)

  lazy val DARK_BRONZE: Color = color(128, 74, 0)

  lazy val COFFEE: Color = color(111, 78, 55)

  lazy val BROWN_BEAR: Color = color(131, 92, 59)

  lazy val RED_DIRT: Color = color(127, 82, 23)

  lazy val SEPIA: Color = color(127, 70, 44)

  lazy val SIENNA: Color = color(160, 82, 45)

  lazy val SADDLEBROWN: Color = color(139, 69, 19)

  lazy val DARK_SIENNA: Color = color(138, 65, 23)

  lazy val SANGRIA: Color = color(126, 56, 23)

  lazy val BLOOD_RED: Color = color(126, 53, 23)

  lazy val CHESTNUT: Color = color(149, 69, 53)

  lazy val CORAL_BROWN: Color = color(158, 70, 56)

  lazy val CHESTNUT_RED: Color = color(195, 74, 44)

  lazy val MAHOGANY: Color = color(192, 64, 0)

  lazy val RED_GOLD: Color = color(235, 84, 6)

  lazy val RED_FOX: Color = color(195, 88, 23)

  lazy val DARK_BISQUE: Color = color(184, 101, 0)

  lazy val LIGHT_BROWN: Color = color(181, 101, 29)

  lazy val PETRA_GOLD: Color = color(183, 103, 52)

  lazy val RUST: Color = color(195, 98, 65)

  lazy val COPPER_RED: Color = color(203, 109, 81)

  lazy val ORANGE_SALMON: Color = color(196, 116, 81)

  lazy val CHOCOLATE: Color = color(210, 105, 30)

  lazy val SEDONA: Color = color(204, 102, 0)

  lazy val PAPAYA_ORANGE: Color = color(229, 103, 23)

  lazy val HALLOWEEN_ORANGE: Color = color(230, 108, 44)

  lazy val NEON_ORANGE: Color = color(255, 103, 0)

  lazy val BRIGHT_ORANGE: Color = color(255, 95, 31)

  lazy val PUMPKIN_ORANGE: Color = color(248, 114, 23)

  lazy val CARROT_ORANGE: Color = color(248, 128, 23)

  lazy val DARKORANGE: Color = color(255, 140, 0)

  lazy val CONSTRUCTION_CONE_ORANGE: Color = color(248, 116, 49)

  lazy val INDIAN_SAFFRON: Color = color(255, 119, 34)

  lazy val SUNRISE_ORANGE: Color = color(230, 116, 81)

  lazy val MANGO_ORANGE: Color = color(255, 128, 64)

  lazy val CORAL: Color = color(255, 127, 80)

  lazy val BASKET_BALL_ORANGE: Color = color(248, 129, 88)

  lazy val LIGHT_SALMON_ROSE: Color = color(249, 150, 107)

  lazy val LIGHTSALMON: Color = color(255, 160, 122)

  lazy val DARKSALMON: Color = color(233, 150, 122)

  lazy val TANGERINE: Color = color(231, 138, 97)

  lazy val LIGHT_COPPER: Color = color(218, 138, 103)

  lazy val SALMON_PINK: Color = color(255, 134, 116)

  lazy val SALMON: Color = color(250, 128, 114)

  lazy val PEACH_PINK: Color = color(249, 139, 136)

  lazy val LIGHTCORAL: Color = color(240, 128, 128)

  lazy val PASTEL_RED: Color = color(246, 114, 128)

  lazy val PINK_CORAL: Color = color(231, 116, 113)

  lazy val BEAN_RED: Color = color(247, 93, 89)

  lazy val VALENTINE_RED: Color = color(229, 84, 81)

  lazy val INDIANRED: Color = color(205, 92, 92)

  lazy val TOMATO: Color = color(255, 99, 71)

  lazy val SHOCKING_ORANGE: Color = color(229, 91, 60)

  lazy val ORANGERED: Color = color(255, 69, 0)

  lazy val RED: Color = color(255, 0, 0)

  lazy val NEON_RED: Color = color(253, 28, 3)

  lazy val SCARLET_RED: Color = color(255, 36, 0)

  lazy val RUBY_RED: Color = color(246, 34, 23)

  lazy val FERRARI_RED: Color = color(247, 13, 26)

  lazy val FIRE_ENGINE_RED: Color = color(246, 40, 23)

  lazy val LAVA_RED: Color = color(228, 34, 23)

  lazy val LOVE_RED: Color = color(228, 27, 23)

  lazy val GRAPEFRUIT: Color = color(220, 56, 31)

  lazy val CHERRY_RED: Color = color(194, 70, 65)

  lazy val CHILLI_PEPPER: Color = color(193, 27, 23)

  lazy val FIREBRICK: Color = color(178, 34, 34)

  lazy val TOMATO_SAUCE_RED: Color = color(178, 24, 7)

  lazy val BROWN: Color = color(165, 42, 42)

  lazy val CARBON_RED: Color = color(167, 13, 42)

  lazy val CRANBERRY: Color = color(159, 0, 15)

  lazy val SAFFRON_RED: Color = color(147, 19, 20)

  lazy val CRIMSON_RED: Color = color(153, 0, 0)

  lazy val WINE_RED: Color = color(153, 0, 18)

  lazy val DARKRED: Color = color(139, 0, 0)

  lazy val MAROON: Color = color(128, 0, 0)

  lazy val BURGUNDY: Color = color(140, 0, 26)

  lazy val VERMILION: Color = color(126, 25, 27)

  lazy val DEEP_RED: Color = color(128, 5, 23)

  lazy val RED_BLOOD: Color = color(102, 0, 0)

  lazy val BLOOD_NIGHT: Color = color(85, 22, 6)

  lazy val DARK_SCARLET: Color = color(86, 3, 25)

  lazy val BLACK_BEAN: Color = color(61, 12, 2)

  lazy val CHOCOLATE_BROWN: Color = color(63, 0, 15)

  lazy val MIDNIGHT: Color = color(43, 27, 23)

  lazy val PURPLE_LILY: Color = color(85, 10, 53)

  lazy val PURPLE_MAROON: Color = color(129, 5, 65)

  lazy val PLUM_PIE: Color = color(125, 5, 65)

  lazy val PLUM_VELVET: Color = color(125, 5, 82)

  lazy val DARK_RASPBERRY: Color = color(135, 38, 87)

  lazy val VELVET_MAROON: Color = color(126, 53, 77)

  lazy val ROSY_FINCH: Color = color(127, 78, 82)

  lazy val DULL_PURPLE: Color = color(127, 82, 93)

  lazy val PUCE: Color = color(127, 90, 88)

  lazy val ROSE_DUST: Color = color(153, 112, 112)

  lazy val PASTEL_BROWN: Color = color(177, 144, 127)

  lazy val ROSY_PINK: Color = color(179, 132, 129)

  lazy val ROSYBROWN: Color = color(188, 143, 143)

  lazy val KHAKI_ROSE: Color = color(197, 144, 142)

  lazy val LIPSTICK_PINK: Color = color(196, 135, 147)

  lazy val PINK_BROWN: Color = color(196, 129, 137)

  lazy val OLD_ROSE: Color = color(192, 128, 129)

  lazy val DUSTY_PINK: Color = color(213, 138, 148)

  lazy val PINK_DAISY: Color = color(231, 153, 163)

  lazy val ROSE: Color = color(232, 173, 170)

  lazy val DUSTY_ROSE: Color = color(201, 169, 166)

  lazy val SILVER_PINK: Color = color(196, 174, 173)

  lazy val GOLD_PINK: Color = color(230, 199, 194)

  lazy val ROSE_GOLD: Color = color(236, 197, 192)

  lazy val DEEP_PEACH: Color = color(255, 203, 164)

  lazy val PASTEL_ORANGE: Color = color(248, 184, 139)

  lazy val DESERT_SAND: Color = color(237, 201, 175)

  lazy val UNBLEACHED_SILK: Color = color(255, 221, 202)

  lazy val PIG_PINK: Color = color(253, 215, 228)

  lazy val PALE_PINK: Color = color(242, 212, 215)

  lazy val BLUSH: Color = color(255, 230, 232)

  lazy val MISTYROSE: Color = color(255, 228, 225)

  lazy val PINK_BUBBLE_GUM: Color = color(255, 223, 221)

  lazy val LIGHT_ROSE: Color = color(251, 207, 205)

  lazy val LIGHT_RED: Color = color(255, 204, 203)

  lazy val WARM_PINK: Color = color(246, 198, 189)

  lazy val DEEP_ROSE: Color = color(251, 187, 185)

  lazy val PINK: Color = color(255, 192, 203)

  lazy val LIGHTPINK: Color = color(255, 182, 193)

  lazy val SOFT_PINK: Color = color(255, 184, 191)

  lazy val DONUT_PINK: Color = color(250, 175, 190)

  lazy val BABY_PINK: Color = color(250, 175, 186)

  lazy val FLAMINGO_PINK: Color = color(249, 167, 176)

  lazy val PASTEL_PINK: Color = color(254, 163, 170)

  lazy val ROSE_PINK: Color = color(231, 161, 176)

  lazy val CADILLAC_PINK: Color = color(227, 138, 174)

  lazy val CARNATION_PINK: Color = color(247, 120, 161)

  lazy val PASTEL_ROSE: Color = color(229, 120, 143)

  lazy val BLUSH_RED: Color = color(229, 110, 148)

  lazy val PALEVIOLETRED: Color = color(219, 112, 147)

  lazy val PURPLE_PINK: Color = color(209, 101, 135)

  lazy val TULIP_PINK: Color = color(194, 90, 124)

  lazy val BASHFUL_PINK: Color = color(194, 82, 131)

  lazy val DARK_PINK: Color = color(231, 84, 128)

  lazy val DARK_HOT_PINK: Color = color(246, 96, 171)

  lazy val HOTPINK: Color = color(255, 105, 180)

  lazy val WATERMELON_PINK: Color = color(252, 108, 133)

  lazy val VIOLET_RED: Color = color(246, 53, 138)

  lazy val HOT_DEEP_PINK: Color = color(245, 40, 135)

  lazy val BRIGHT_PINK: Color = color(255, 0, 127)

  lazy val DEEPPINK: Color = color(255, 20, 147)

  lazy val NEON_PINK: Color = color(245, 53, 170)

  lazy val CHROME_PINK: Color = color(255, 51, 170)

  lazy val NEON_HOT_PINK: Color = color(253, 52, 156)

  lazy val PINK_CUPCAKE: Color = color(228, 94, 157)

  lazy val ROYAL_PINK: Color = color(231, 89, 172)

  lazy val DIMORPHOTHECA_MAGENTA: Color = color(227, 49, 157)

  lazy val PINK_LEMONADE: Color = color(228, 40, 124)

  lazy val RED_PINK: Color = color(250, 42, 85)

  lazy val RASPBERRY: Color = color(227, 11, 93)

  lazy val CRIMSON: Color = color(220, 20, 60)

  lazy val BRIGHT_MAROON: Color = color(195, 33, 72)

  lazy val ROSE_RED: Color = color(194, 30, 86)

  lazy val ROGUE_PINK: Color = color(193, 40, 105)

  lazy val BURNT_PINK: Color = color(193, 34, 103)

  lazy val PINK_VIOLET: Color = color(202, 34, 107)

  lazy val MAGENTA_PINK: Color = color(204, 51, 139)

  lazy val MEDIUMVIOLETRED: Color = color(199, 21, 133)

  lazy val DARK_CARNATION_PINK: Color = color(193, 34, 131)

  lazy val RASPBERRY_PURPLE: Color = color(179, 68, 108)

  lazy val PINK_PLUM: Color = color(185, 59, 143)

  lazy val ORCHID: Color = color(218, 112, 214)

  lazy val DEEP_MAUVE: Color = color(223, 115, 212)

  lazy val VIOLET: Color = color(238, 130, 238)

  lazy val FUCHSIA_PINK: Color = color(255, 119, 255)

  lazy val BRIGHT_NEON_PINK: Color = color(244, 51, 255)

  lazy val MAGENTA: Color = color(255, 0, 255)

  lazy val CRIMSON_PURPLE: Color = color(226, 56, 236)

  lazy val HELIOTROPE_PURPLE: Color = color(212, 98, 255)

  lazy val TYRIAN_PURPLE: Color = color(196, 90, 236)

  lazy val MEDIUMORCHID: Color = color(186, 85, 211)

  lazy val PURPLE_FLOWER: Color = color(167, 74, 199)

  lazy val ORCHID_PURPLE: Color = color(176, 72, 181)

  lazy val RICH_LILAC: Color = color(182, 102, 210)

  lazy val PASTEL_VIOLET: Color = color(210, 145, 188)

  lazy val MAUVE_TAUPE: Color = color(145, 95, 109)

  lazy val VIOLA_PURPLE: Color = color(126, 88, 126)

  lazy val EGGPLANT: Color = color(97, 64, 81)

  lazy val PLUM_PURPLE: Color = color(88, 55, 89)

  lazy val GRAPE: Color = color(94, 90, 128)

  lazy val PURPLE_NAVY: Color = color(78, 81, 128)

  lazy val SLATEBLUE: Color = color(106, 90, 205)

  lazy val BLUE_LOTUS: Color = color(105, 96, 236)

  lazy val BLURPLE: Color = color(88, 101, 242)

  lazy val LIGHT_SLATE_BLUE: Color = color(115, 106, 255)

  lazy val MEDIUMSLATEBLUE: Color = color(123, 104, 238)

  lazy val PERIWINKLE_PURPLE: Color = color(117, 117, 207)

  lazy val VERY_PERI: Color = color(102, 103, 171)

  lazy val BRIGHT_GRAPE: Color = color(111, 45, 168)

  lazy val PURPLE_AMETHYST: Color = color(108, 45, 199)

  lazy val BRIGHT_PURPLE: Color = color(106, 13, 173)

  lazy val DEEP_PERIWINKLE: Color = color(84, 83, 166)

  lazy val DARKSLATEBLUE: Color = color(72, 61, 139)

  lazy val PURPLE_HAZE: Color = color(78, 56, 126)

  lazy val PURPLE_IRIS: Color = color(87, 27, 126)

  lazy val DARK_PURPLE: Color = color(75, 1, 80)

  lazy val DEEP_PURPLE: Color = color(54, 1, 63)

  lazy val MIDNIGHT_PURPLE: Color = color(46, 26, 71)

  lazy val PURPLE_MONSTER: Color = color(70, 27, 126)

  lazy val INDIGO: Color = color(75, 0, 130)

  lazy val BLUE_WHALE: Color = color(52, 45, 126)

  lazy val REBECCAPURPLE: Color = color(102, 51, 153)

  lazy val PURPLE_JAM: Color = color(106, 40, 126)

  lazy val DARKMAGENTA: Color = color(139, 0, 139)

  lazy val PURPLE: Color = color(128, 0, 128)

  lazy val FRENCH_LILAC: Color = color(134, 96, 142)

  lazy val DARKORCHID: Color = color(153, 50, 204)

  lazy val DARKVIOLET: Color = color(148, 0, 211)

  lazy val PURPLE_VIOLET: Color = color(141, 56, 201)

  lazy val JASMINE_PURPLE: Color = color(162, 59, 236)

  lazy val PURPLE_DAFFODIL: Color = color(176, 65, 255)

  lazy val CLEMATIS_VIOLET: Color = color(132, 45, 206)

  lazy val BLUEVIOLET: Color = color(138, 43, 226)

  lazy val PURPLE_SAGE_BUSH: Color = color(122, 93, 199)

  lazy val LOVELY_PURPLE: Color = color(127, 56, 236)

  lazy val NEON_PURPLE: Color = color(157, 0, 255)

  lazy val PURPLE_PLUM: Color = color(142, 53, 239)

  lazy val AZTECH_PURPLE: Color = color(137, 59, 255)

  lazy val MEDIUMPURPLE: Color = color(147, 112, 219)

  lazy val LIGHT_PURPLE: Color = color(132, 103, 215)

  lazy val CROCUS_PURPLE: Color = color(145, 114, 236)

  lazy val PURPLE_MIMOSA: Color = color(158, 123, 255)

  lazy val PERIWINKLE: Color = color(204, 204, 255)

  lazy val PALE_LILAC: Color = color(220, 208, 255)

  lazy val LAVENDER_PURPLE: Color = color(150, 123, 182)

  lazy val ROSE_PURPLE: Color = color(176, 159, 202)

  lazy val LILAC: Color = color(200, 162, 200)

  lazy val MAUVE: Color = color(224, 176, 255)

  lazy val BRIGHT_LILAC: Color = color(216, 145, 239)

  lazy val PURPLE_DRAGON: Color = color(195, 142, 199)

  lazy val PLUM: Color = color(221, 160, 221)

  lazy val BLUSH_PINK: Color = color(230, 169, 236)

  lazy val PASTEL_PURPLE: Color = color(242, 162, 232)

  lazy val BLOSSOM_PINK: Color = color(249, 183, 255)

  lazy val WISTERIA_PURPLE: Color = color(198, 174, 199)

  lazy val PURPLE_THISTLE: Color = color(210, 185, 211)

  lazy val THISTLE: Color = color(216, 191, 216)

  lazy val PURPLE_WHITE: Color = color(223, 211, 227)

  lazy val PERIWINKLE_PINK: Color = color(233, 207, 236)

  lazy val COTTON_CANDY: Color = color(252, 223, 255)

  lazy val LAVENDER_PINOCCHIO: Color = color(235, 221, 226)

  lazy val DARK_WHITE: Color = color(225, 217, 209)

  lazy val ASH_WHITE: Color = color(233, 228, 212)

  lazy val WHITE_CHOCOLATE: Color = color(237, 230, 214)

  lazy val SOFT_IVORY: Color = color(250, 240, 221)

  lazy val OFF_WHITE: Color = color(248, 240, 227)

  lazy val PEARL_WHITE: Color = color(248, 246, 240)

  lazy val RED_WHITE: Color = color(243, 232, 234)

  lazy val LAVENDERBLUSH: Color = color(255, 240, 245)

  lazy val PEARL: Color = color(253, 238, 244)

  lazy val EGG_SHELL: Color = color(255, 249, 227)

  lazy val OLDLACE: Color = color(254, 240, 227)

  lazy val LINEN: Color = color(250, 240, 230)

  lazy val SEASHELL: Color = color(255, 245, 238)

  lazy val BONE_WHITE: Color = color(249, 246, 238)

  lazy val RICE: Color = color(250, 245, 239)

  lazy val FLORALWHITE: Color = color(255, 250, 240)

  lazy val IVORY: Color = color(255, 255, 240)

  lazy val WHITE_GOLD: Color = color(255, 255, 244)

  lazy val LIGHT_WHITE: Color = color(255, 255, 247)

  lazy val WHITESMOKE: Color = color(245, 245, 245)

  lazy val COTTON: Color = color(251, 251, 249)

  lazy val SNOW: Color = color(255, 250, 250)

  lazy val MILK_WHITE: Color = color(254, 252, 255)

  lazy val HALF_WHITE: Color = color(255, 254, 250)

  lazy val WHITE: Color = color(255, 255, 255)

}
