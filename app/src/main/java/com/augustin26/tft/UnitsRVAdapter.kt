package com.augustin26.tft

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orhanobut.logger.Logger
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class UnitsRVAdapter(private val context: Context, private val units : JSONArray) : RecyclerView.Adapter<UnitsRVAdapter.ViewHolder>() {

    private val const = Const(context)

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgUnitTier: ImageView = itemView.findViewById<ImageView>(R.id.img_unit_tier)
        val imgUnit: ImageView = itemView.findViewById<ImageView>(R.id.img_unit)
        val item1 = itemView.findViewById<ImageView>(R.id.img_item1)
        val item2 = itemView.findViewById<ImageView>(R.id.img_item2)
        val item3 = itemView.findViewById<ImageView>(R.id.img_item3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.unit_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //유닛 이미지
        val unitName = JSONObject(units[position].toString()).get("character_id").toString().split("_")[1]
        var url = when (unitName) {
            "Ahri" -> "https://cdn.lolchess.gg/upload/images/champions/Ahri_1643374814.png"
            "Alistar" -> "https://cdn.lolchess.gg/upload/images/champions/Alistar_1643374823.png"
            "Ashe" -> "https://cdn.lolchess.gg/upload/images/champions/Ashe_1643374833.png"
            "Brand" -> "https://cdn.lolchess.gg/upload/images/champions/Brand_1643374847.png"
            "Braum" -> "https://cdn.lolchess.gg/upload/images/champions/Braum_1634784638.png"
            "Caitlyn" -> "https://cdn.lolchess.gg/upload/images/champions/Caitlyn_1635139772.png"
            "ChoGath" -> "https://ddragon.poro.gg/12.3.1/img/champion/Chogath.png"
            "Darius" -> "https://cdn.lolchess.gg/upload/images/champions/Darius_1634784329.png"
            "Draven" -> "https://cdn.lolchess.gg/upload/images/champions/Draven_1643374858.png"
            "Galio" -> "https://cdn.lolchess.gg/upload/images/champions/Galio_1634784242.png"
            "Gnar" -> "https://cdn.lolchess.gg/upload/images/champions/Gnar_1643374869.png"
            "Irelia" -> "https://cdn.lolchess.gg/upload/images/champions/Irelia_1643374879.png"
            "JarvanIV" -> "https://cdn.lolchess.gg/upload/images/champions/JarvanIV_1643374909.png"
            "Jayce" -> "https://cdn.lolchess.gg/upload/images/champions/Jayce_1635133912.png"
            "KhaZix" -> "https://ddragon.poro.gg/12.3.1/img/champion/Khazix.png"
            "Leona" -> "https://cdn.lolchess.gg/upload/images/champions/Leona_1643374919.png"
            "Lucian" -> "https://cdn.lolchess.gg/upload/images/champions/Lucian_1643374925.png"
            "MissFortune" -> "https://cdn.lolchess.gg/upload/images/champions/MissFortune_1634784448.png"
            "Morgana" -> "https://cdn.lolchess.gg/upload/images/champions/Morgana_1643374932.png"
            "Nocturne" -> "https://cdn.lolchess.gg/upload/images/champions/Nocturne_1643374939.png"
            "Quinn" -> "https://cdn.lolchess.gg/upload/images/champions/Quinn_1634785593.png"
            "Renata" -> "https://cdn.lolchess.gg/upload/images/champions/Renata_1643374746.png"
            "Sejuani" -> "https://cdn.lolchess.gg/upload/images/champions/Sejuani_1643374954.png"
            "Senna" -> "https://cdn.lolchess.gg/upload/images/champions/Senna_1643374978.png"
            "Silco" -> "https://cdn.lolchess.gg/upload/images/champions/Silco_1643374772.png"
            "Sivir" -> "https://cdn.lolchess.gg/upload/images/champions/Sivir_1643374987.png"
            "Syndra" -> "https://cdn.lolchess.gg/upload/images/champions/Syndra_1643374998.png"
            "Talon" -> "https://cdn.lolchess.gg/upload/images/champions/Talon_1643375005.png"
            "Tryndamere" -> "https://cdn.lolchess.gg/upload/images/champions/Tryndamere_1643375011.png"
            "Veigar" -> "https://cdn.lolchess.gg/upload/images/champions/Veigar_1643779403.png"
            "Zeri" -> "https://cdn.lolchess.gg/upload/images/champions/Zeri_1643374799.png"
            "Zyra" -> "https://cdn.lolchess.gg/upload/images/champions/Zyra_1634785008.png"
            else -> "${const.championIcon}$unitName.png"
        }
        Glide.with(context)//유닛 아이콘
            .load(url)
            .into(holder.imgUnit)

        //아이템 이미지
        val items = JSONArray(JSONObject(units[position].toString()).get("items").toString())
        val listdata = ArrayList<String>()
        try {
            listdata.add(items[0].toString())
            listdata.add(items[1].toString())
            listdata.add(items[2].toString())
        }catch (e: Exception) { }
        listdata.forEachIndexed { index, s ->
            val holder = when (index) {
                0->holder.item1
                1->holder.item2
                2->holder.item3
                else -> return@forEachIndexed
            }
            val url = when (s) {
                "1"->"https://cdn.lolchess.gg/upload/images/items/1038_H6kWlJGUpYCdjIDuXZR3DdEUWq4xEeV2TpHk4Jw4.png" //BF대검
                "2"->"https://cdn.lolchess.gg/upload/images/items/RecurveBow_1640058784.png" //곡궁
                "3"->"https://cdn.lolchess.gg/upload/images/items/NeedlesslyLargeRod_1640059008.png" //쓸큰지
                "4"->"https://cdn.lolchess.gg/upload/images/items/Tearofthegoddess_1640059037.png" //여눈
                "5"->"https://cdn.lolchess.gg/upload/images/items/ChainVest_1640058945.png" //사슬갑옷
                "7"->"https://cdn.lolchess.gg/upload/images/items/1011_kcGXA0ld8Wx0BgfSCGAZfhWPir4tc24Oi3QNzP9B.png" //거인의허리띠
                "9"->"https://cdn.lolchess.gg/upload/images/items/SparringGloves_1640059055.png" //연습용장갑
                "11"->"https://cdn.lolchess.gg/images/tft/item/LordsEdge.png" //죽검
                "12"->"https://cdn.lolchess.gg/images/tft/item/GiantSlayer.png" //거학
                "13"->"https://cdn.lolchess.gg/upload/images/items/HextechGunblade_1640058876.png" //총검
                "14"->"https://cdn.lolchess.gg/upload/images/items/SpearofShojin_1642015208.png" //쇼진
                "16"->"https://cdn.lolchess.gg/upload/images/items/Bloodthirster_1640059177.png" //피바라기
                "17"->"https://cdn.lolchess.gg/upload/images/items/ZekesHerald_1640059135.png" //지크
                "19"->"https://cdn.lolchess.gg/upload/images/items/InfinityEdge_ljmJbkViyMGC8IKr50os4jC8Ccl1ro2JbqXuvHqT.png" //무한의대검
                "22"->"https://cdn.lolchess.gg/upload/images/items/3094_SZmtvKzywMHMFOPSyWM1jysci2VFB13EmWzpUkh1.png" //고속연사포
                "23"->"https://cdn.lolchess.gg/upload/images/items/GuinsoosRageblade_1642015181.png" //구인수
                "24"->"https://cdn.lolchess.gg/upload/images/items/StatikkShiv_1640058978.png" //스태틱
                "25"->"https://cdn.lolchess.gg/upload/images/items/TitansResolve_1640058512.png" //거인의결의
                "26"->"https://cdn.lolchess.gg/upload/images/items/RunaansHurricane_1640058427.png" //루난
                "27"->"https://cdn.lolchess.gg/images/tft/item/ZzRotPortal.png" //즈롯차원문
                "28"->"https://cdn.lolchess.gg/upload/images/items/DuelistsZeal_1634786429.png" //도전자
                "29"->"https://cdn.lolchess.gg/upload/images/items/LastWhisper_1642015257.png" //최후의속삭임
                "33"->"https://cdn.lolchess.gg/upload/images/items/RabadonsDeathcap_1640058833.png" //라바돈
                "34"->"https://cdn.lolchess.gg/upload/images/items/3285_8H5TqcPL1z03UB6hTyP9x6N5hZ9jVJIcf6YhxpUg.png" //대천사
                "35"->"https://cdn.lolchess.gg/upload/images/items/LocketoftheIronSolari_YOaQeJjb0jXhQnypCCvBBX2TNxqgICIFAp6lyvqz.png" //솔라리
                "36"->"https://cdn.lolchess.gg/upload/images/items/IonicSpark_1640059091.png" //이온충격기
                "37"->"https://cdn.lolchess.gg/upload/images/items/Morellonomicon_1640058393.png" //모렐로노미콘
                "39"->"https://cdn.lolchess.gg/images/tft/item/ArcaneGauntlet.png" //보건
                "44"->"https://cdn.lolchess.gg/images/tft/item/BlueBuff.png" //블루
                "45"->"https://cdn.lolchess.gg/upload/images/items/3110_v0619r67zsjsowMlFniZvXGsVNfdNDwoYbeCcVFX.png" //얼어붙은심장
                "46"->"https://cdn.lolchess.gg/upload/images/items/Chalice_D96IpanZVyS5SqA0PxLDfDp7FVAV7WGz6iCZaVLC.png" //성배
                "47"->"https://cdn.lolchess.gg/upload/images/items/Redemption_1642015166.png" //구원
                "49"->"https://cdn.lolchess.gg/images/tft/item/HandofJustice.png" //정손
                "55"->"https://cdn.lolchess.gg/upload/images/items/BrambleVest_1640058438.png" //덤불조끼
                "56"->"https://cdn.lolchess.gg/upload/images/items/IronWill_AnyIyxtEcc5fby5mgL1NNIwzm76wiG0yxwcPQ7nj.png" //가고일의돌갑옷
                "57"->"https://cdn.lolchess.gg/upload/images/items/SunfireCape_1640059158.png" //태불망
                "59"->"https://cdn.lolchess.gg/upload/images/items/Shroud_EgjmR23WSHz0nbvbfOk5UYUAmGDsNO0iEPNjl7za.png" //침장
                "66"->"https://cdn.lolchess.gg/upload/images/items/DragonsClaw_1640058416.png" //용의발톱
                "67"->"https://cdn.lolchess.gg/images/tft/item/Zephyr.png" //서풍
                "69"->"https://cdn.lolchess.gg/images/tft/item/Quicksilver.png" //수은
                "70"->"https://cdn.lolchess.gg/upload/images/items/VanguardsCuirass_1645157511.png" //연미복
                "77"->"https://cdn.lolchess.gg/upload/images/items/3083_C5Y1EvfU08Ug9wCaCJTEp2hNRjh5zSkq9eoY97Uc.png" //워모그
                "78"->"https://cdn.lolchess.gg/upload/images/items/WarlordsBanner_1634786621.png" //화학공학
                "79"->"https://cdn.lolchess.gg/images/tft/item/Backhand.png" //덫발톱
                "88"->"https://cdn.lolchess.gg/upload/images/items/ForceofNature_1640059110.png" //전략가의왕관
                "89"->"https://cdn.lolchess.gg/upload/images/items/YoumuusGhostblade_1634786635.png" //암살자
                "99"->"https://cdn.lolchess.gg/upload/images/items/ThiefsGloves_9PTkdu1Iyw6L1voQbNlsP5U74TzF7suIeq5RQjbh.png" //도적의장갑
                "2190"->"https://cdn.lolchess.gg/upload/images/items/ElderwoodHeirloom_1634786467.png" //돌연변이
                "2192"->"https://cdn.lolchess.gg/upload/images/items/MercenaryEmblem_1634868949.png" //용병
                "2195"->"https://cdn.lolchess.gg/upload/images/items/ScrapEmblem_1634869013.png" //고물상
                "2197"->"https://cdn.lolchess.gg/upload/images/items/BruiserEmblem_1634868865.png" //난동꾼
                else -> return@forEachIndexed
            }
            Glide.with(context)//소환사 아이콘
                .load(url)
                .into(holder)
        }

        //유닛 티어 이미지
        val rarity = JSONObject(units[position].toString()).get("rarity").toString()
        val tier = JSONObject(units[position].toString()).get("tier").toString()
        url = const.championStars + "cost${rarity.toInt()+1}_stars${tier}" + ".png"
        Glide.with(context)//소환사 아이콘
            .load(url)
            .into(holder.imgUnitTier)
        when (rarity.toInt()) {
            0-> holder.imgUnit.foreground = ContextCompat.getDrawable(context, R.drawable.rarity1_rounded)
            1-> holder.imgUnit.foreground = ContextCompat.getDrawable(context, R.drawable.rarity2_rounded)
            2-> holder.imgUnit.foreground = ContextCompat.getDrawable(context, R.drawable.rarity3_rounded)
            3-> holder.imgUnit.foreground = ContextCompat.getDrawable(context, R.drawable.rarity4_rounded)
            4-> holder.imgUnit.foreground = ContextCompat.getDrawable(context, R.drawable.rarity5_rounded)
        }

    }

    override fun getItemCount(): Int {
        return units.length()
    }
}
