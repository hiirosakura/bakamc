# BakaMC

## Config

<details>
<summary>配置文件模板</summary>

```json
{
  "database": {
    "password": "root",
    "url": "jdbc:mysql://localhost:3306/bakamc?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai",
    "user": "root"
  },
  "block_infos": {
    "西瓜": {
      "type": "minecraft:melon_block"
    },
    "主世界方块": {
      "world": "minecraft:overworld"
    }
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
    "change_block_map": {
      "小黑": [
        "主世界方块 -> minecraft:air"
      ]
    },
    "explode_block_map": {
      "苦力怕": [
        "西瓜"
      ]
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
    },
    "energy_bar": {
      "color": "GREEN",
      "style": "SEGMENTED_10",
      "title": "飞行能量: %.2f(%+.2f)/%.2f"
    }
  }
}
```

</details>

### 配置文件详解

**`database` 数据库相关配置**

| 属性名        |    类型    | 描述      |
|:-----------|:--------:|:--------|
| `url`      | `String` | 数据库连接地址 |
| `root`     | `String` | 数据库用户名  |
| `password` | `String` | 数据库用户密码 |

**`block_infos` 记录特定的方块信息**

| 属性名               |     类型      | 描述                     |
|:------------------|:-----------:|:-----------------------|
| `Key`             |  `String`   | 方块索引                   |
| `BlockInfo.x`     | `IntRange?` | 方块所在的 `x` 轴范围,为空时不参与判断 |
| `BlockInfo.y`     | `IntRange?` | 方块所在的 `y` 轴范围,为空时不参与判断 |
| `BlockInfo.z`     | `IntRange?` | 方块所在的 `z` 轴范围,为空时不参与判断 |
| `BlockInfo.type`  |  `String?`  | 方块的类型,为空时不参与判断         |
| `BlockInfo.biome` |  `String?`  | 方块所在生物群戏,为空时不参与判断      |
| `BlockInfo.world` |  `String?`  | 方块所在世界,为空时不参与判断        |

```json
{
  "block_infos": {
    "西瓜": {
      "x": "0..55",
      "y": "-10..55",
      "z": "20..60",
      "type": "minecraft:melon",
      "biome": "minecraft:plains",
      "world": "minecraft:overworld"
    }
  }
}
```

将会判断的方块,所有属性为空时不匹配任何方块

- 方块X坐标范围为 `0<= x <=55`
- 方块Y坐标范围为 `-10<= x <=55`
- 方块Z坐标范围为 `20<= x <=60`
- 方块类型为 `minecraft:melon` 西瓜方块
- 方块所在生物群系为 `minecraft:plains` 平原
- 方块所在世界为 `minecraft:overworld` 主世界

**`entity` 实体相关配置**

| 属性名                 |                 类型                  | 描述           |
|:--------------------|:-----------------------------------:|:-------------|
| `entity_infos`      |        `Map<Key,EntityInfo>`        | 记录特定的实体信息    |
| `change_block_map`  | `Map<EntityInfo,List<BlcokChange>>` | 实体更改方块状态相关映射 |
| `explode_block_map` |  `Map<EntityInfo,List<BlcokInfo>>`  | 实体爆炸破坏方块相关映射 |

**`entity_infos` 纪录特定的实体信息**

| 属性名               |    类型     | 描述                |
|:------------------|:---------:|:------------------|
| `Key`             | `String`  | 实体索引              |
| `EntityInfo.name` | `String?` | 实体名,为空时不参与判断      |
| `EntityInfo.type` | `String?` | 实体类型,为空时不参与判断     |
| `EntityInfo.uuid` | `String?` | 实体`UUID`,为空时不参与判断 |

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

将会判断的实体,索引`<key>`为`小黑`,所有属性为空时不会匹配任何实体

- 实体类型为`minecraft:enderman`
- 实体名为`末影人`
- 实体UUID为`808e8bd1-f808-4dec-aa46-ad64d3d6dc1c`

**`change_block_map` 实体改变方块相关配置**

| 属性名                |           类型            | 描述                                                                                                                        |
|:-------------------|:-----------------------:|:--------------------------------------------------------------------------------------------------------------------------|
| `Key`              | `String => EntityInfo`  | 会从`entity_infos`查找对应的`<Key>`,没有则会将字符串作为`EntityInfo(type = <value>)`解析                                                     |
| `BlcokChange`      | `String => BlcokChange` | 格式为`BlcokChange.from -> BlcokChange.to`,如果没有找到` -> `则会将字符串作为`BlcokChange(from = BlockInfo(type = <value>), to = nulll)`解析 |
| `BlcokChange.from` |  `String => BlockInfo`  | 会从`block_infos`查找对应的`<Key>`,没有则会将字符串作为`BlockInfo(type = <value>)`解析                                                       |
| `BlcokChange.to`   |  `String => BlockInfo`  | 会从`block_infos`查找对应的`<Key>`,没有则会将字符串作为`BlockInfo(type = <value>)`解析                                                       |

**`explode_block_map` 实体爆炸破坏方块映射**

| 属性名         |           类型            | 描述                                                                    |
|:------------|:-----------------------:|:----------------------------------------------------------------------|
| `key`       | `String => EntitiyInfo` | 会从`entity_infos`查找对应的`<Key>`,没有则会将字符串作为`EntityInfo(type = <value>)`解析 |
| `BlcokInfo` |  `String => BlcokInfo`  | 会从`block_infos`查找对应的`<Key>`,没有则会将字符串作为`BlockInfo(type = <value>)`解析   |

```json
{
  "explode_block_map": {
    "苦力怕": [
      "西瓜",
      "minecraft:dirt"
    ]
  }
}
```

将会禁止实体`苦力怕`爆炸时破坏方块`西瓜`以及方块`BlockInfo{ type = "minecraft:dirt" }`

**`flight_energy` 飞行能量相关配置**

| 属性名           |          类型          | 描述                                          |
|:--------------|:--------------------:|:--------------------------------------------|
| `energy_cost` |       `Double`       | 每一次`Tick`消耗的能量                              |
| `max_energy`  |       `Double`       | 玩家拥有的最大能量值                                  |
| `money_item`  | `Map<String,Double>` | 用于购买飞行能量的物品`key为特殊物品的key,value为该物品每个能兑换的能量` |
| `sync_period` |      `Duration`      | 同步周期,用于同步玩家的能量值到数据库                         |
| `tick_period` |      `Duration`      | `Tick`周期                                    |
| `energy_bar`  |     `EnergyBar`      | 能量条相关配置                                     |

`Duration`

| 属性名     |       类型       | 描述                                                                                 |
|:--------|:--------------:|:-----------------------------------------------------------------------------------|
| `value` |    `Number`    | 时间                                                                                 |
| `unit`  | `DurationUnit` | 时间单位,可选值`[NANOSECONDS, MICROSECONDS, MILLISECONDS, SECONDS, MINUTES, HOURS, DAYS]` |

`EnergyBar`

| 属性名     |     类型     | 描述                                                                          |
|:--------|:----------:|:----------------------------------------------------------------------------|
| `color` | `BarColor` | 能量调颜色,可选值`[PINK,BLUE,RED,GREEN,YELLOW,PURPLE,WHITE]`                        |
| `style` | `BarStyle` | 能量条样式,可选值`[SOLID， SEGMENTED_6， SEGMENTED_10， SEGMENTED_12， SEGMENTED_20]`   |
| `title` |  `String`  | 能量条标题文本,三个参数为`玩家当前能量值`,`当前能量值 - 上次计算的能量值`,`最大能量值<max_energy>`,类型为`<Double>` |


## 指令

> `/fly` `execute:切换飞行状态`
> > `recharge <money_item> <count>` `execute:使用<money_item>兑换<count>个物品`
> > > `<money_item:String>` 用于兑换飞行能量的物品`<Key>`会自动提示
>
> > > `<count:Int?>` `null` -> `1` 用于兑换的物品数量
>
> > `energy` `execute:查看当前剩余飞行能量`

### 管理员指令

> `/specialitem` `execute:显示当前所有的特殊物品`
> > `give` `<key>` `<count>` `execute:给予指令发送者<count>个<key>物品`
> > > `<key:String>` 特殊物品的`key`会自动提示
>
> > > `<count:Int?>` `null` -> `1` 物品数量
>
> > `put` `<key>` `execute:将特殊物品<key>更新为手持的物品`
> > > `<key:String?>` `null` -> `item.hoverName`
>
> > `remove` `<key>` `execute:将特殊物品<key>从数据库中删除`
> > > `<key:String>` 特殊物品的`key`会自动提示