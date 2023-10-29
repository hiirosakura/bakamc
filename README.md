# BakaMC

## Config
***

<details>
<summary>配置文件模板</summary>

```json
{
  "database": {
    "url": "数据库连接地址:jdbc:mysql://localhost:3306/bakamc",
    "user": "数据库用户名",
    "password": "数据库用户密码"
  },
  "entity": {
    "entity_infos": {
      "小黑": {
        "type": "minecraft:enderman"
      },
      "苦力怕": {
        "type": "minecraft:creeper"
      }
    },
    "change_block": {
      "block_infos": {
        "方块键": {
          "x": "<IntRange> 方块X坐标范围 null->不判断",
          "y": "<IntRange> 方块Y坐标范围 null->不判断",
          "z": "<IntRange> 方块Z坐标范围 null->不判断",
          "type": "<String> 方块类型 null->不判断",
          "biome": "<String> 方块所在生物群系 null->不判断",
          "world": "<String> 方块所在世界 null->不判断"
        },
        "西瓜": {
          "type": "minecraft:melon_block"
        },
        "主世界方块": {
          "world": "minecraft:overworld"
        }
      },
      "entity_map": {
        "小黑": [
          "主世界方块"
        ],
        "苦力怕": [
          "主世界方块"
        ]
      }
    }
  },
  "flight_energy": {
    "energy_cost": 1.0,
    "max_energy": 5000.0,
    "money_item": {
      "⑨币": 5000.0,
      "冰辉石": 78.125
    },
    "sync_period": {
      "value": 5.0,
      "unit": "MINUTES"
    },
    "tick_period": {
      "value": 1.0,
      "unit": "SECONDS"
    }
  }
}
```

</details>

### 配置文件详解

<details>
<summary>database:数据库相关配置</summary>

| 属性名        |    类型    | 描述      |
|:-----------|:--------:|:--------|
| `url`      | `String` | 数据库连接地址 |
| `root`     | `String` | 数据库用户名  |
| `password` | `String` | 数据库用户密码 |

</details>

<details>
<summary>entity:实体相关配置</summary>

| 属性名            |            类型            | 描述         |
|:---------------|:------------------------:|:-----------|
| `entity_infos` | `Map<String,EntityInfo>` | 记录特定的实体信息  |
| `change_block` |         `Object`         | 实体改变方块相关配置 |

`entity_infos` : 纪录特定的实体信息

| 属性名               |    类型     | 描述              |
|:------------------|:---------:|:----------------|
| `key`             | `String`  | 实体索引            |
| `EntityInfo.name` | `String?` | 实体名,为空时不参与判断    |
| `EntityInfo.type` | `String?` | 实体类型,为空时不参与判断   |
| `EntityInfo.uuid` | `String?` | 实体UUID,为空时不参与判断 |

<details>
<summary>示例</summary>

```json
{
  "entity_infos": {
    "小黑": {
      "type": "minecraft:enderman",
      "name": "末影人",
      "uuid": "808e8bd1-f808-4dec-aa46-ad64d3d6dc1c"
    }
  }
}
```

将会判断的实体，索引`<key>`为`小黑`

- 实体类型为`minecraft:enderman`
- 实体名为`末影人`
- 实体UUID为`808e8bd1-f808-4dec-aa46-ad64d3d6dc1c`

</details>

`change_block`:**实体改变方块相关配置**

| 属性名           |             类型             | 描述                                                      |
|:--------------|:--------------------------:|:--------------------------------------------------------|
| `block_infos` |  `Map<String,BlockInfo>`   | 方块索引                                                    |
| `entity_map`  | `Map<String,List<String>>` | 实体映射 `key:<entity_infos.key>` : `List<block_infos.key>` |

`block_infos` 记录特定的方块信息

| 属性名               |     类型      | 描述                     |
|:------------------|:-----------:|:-----------------------|
| `key`             |  `String`   | 方块索引                   |
| `BlockInfo.x`     | `IntRange?` | 方块所在的 `x` 轴范围,为空时不参与判断 |
| `BlockInfo.y`     | `IntRange?` | 方块所在的 `y` 轴范围,为空时不参与判断 |
| `BlockInfo.z`     | `IntRange?` | 方块所在的 `z` 轴范围,为空时不参与判断 |
| `BlockInfo.type`  |  `String?`  | 方块的类型,为空时不参与判断         |
| `BlockInfo.biome` |  `String?`  | 方块所在生物群戏,为空时不参与判断      |
| `BlockInfo.world` |  `String?`  | 方块所在世界,为空时不参与判断        |

<details>
<summary>示例</summary>

```json
{
  "block_infos": {
    "西瓜": {
      "x": "0..55",
      "y": "-10..55",
      "z": "20..60",
      "type": "minecraft:melon_block",
      "biome": "minecraft:plains",
      "world": "minecraft:overworld"
    }
  }
}
```

将会判断的方块

- 方块X坐标范围为 `0<= x <=55`
- 方块Y坐标范围为 `-10<= x <=55`
- 方块Z坐标范围为 `20<= x <=60`
- 方块类型为 `minecraft:melon_block` 西瓜方块
- 方块所在生物群系为 `minecraft:plains` 平原
- 方块所在世界为 `minecraft:overworld` 主世界

</details>

`entity_map` 实体映射

| 属性名     |       类型       | 描述                         |
|:--------|:--------------:|:---------------------------|
| `key`   |    `String`    | 实体索引 `<entity_infos.key>`  |
| `value` | `List<String>` | 方块索引列表 `<block_infos.key>` |

<details>
<summary>示例</summary>

```json
{
  "entity_map": {
    "小黑": [
      "西瓜"
    ]
  }
}
```

将会禁止实体`小黑`更改方块`西瓜`

</details>

</details>

<details>
<summary>flight_energy:飞行能量相关配置</summary>

| 属性名           |          类型          | 描述                                          |
|:--------------|:--------------------:|:--------------------------------------------|
| `energy_cost` |       `Double`       | 每一次`Tick`消耗的能量                              |
| `max_energy`  |       `Double`       | 玩家拥有的最大能量值                                  |
| `money_item`  | `Map<String,Double>` | 用于购买飞行能量的物品`key为特殊物品的key,value为该物品每个能兑换的能量` |
| `sync_period` |      `Duration`      | 同步周期,用于同步玩家的能量值到数据库                         |
| `tick_period` |      `Duration`      | `Tick`周期                                    |

`Duration`

| 属性名     |       类型       | 描述                                                                                 |
|:--------|:--------------:|:-----------------------------------------------------------------------------------|
| `value` |    `Number`    | 时间                                                                                 |
| `unit`  | `DurationUnit` | 时间单位,可选值`[NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS]` |

</details>

## 指令
***
