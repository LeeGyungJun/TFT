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
            "Renata" -> "https://cdn.lolchess.gg/upload/images/champions/Renata_1643374746.png"
            "ChoGath" -> "https://ddragon.poro.gg/12.3.1/img/champion/Chogath.png"
            "KhaZix" -> "https://ddragon.poro.gg/12.3.1/img/champion/Khazix.png"
            "Ahri" -> "https://cdn.lolchess.gg/upload/images/champions/Ahri_1643374814.png"
            "Lucian" -> "https://cdn.lolchess.gg/upload/images/champions/Lucian_1643374925.png"
            "Gnar" -> "https://cdn.lolchess.gg/upload/images/champions/Gnar_1643374869.png"
            "Zyra" -> "https://cdn.lolchess.gg/upload/images/champions/Zyra_1634785008.png"
            "Alistar" -> "https://cdn.lolchess.gg/upload/images/champions/Alistar_1643374823.png"
            "Braum" -> "https://cdn.lolchess.gg/upload/images/champions/Braum_1634784638.png"
            "Morgana" -> "https://cdn.lolchess.gg/upload/images/champions/Morgana_1643374932.png"
            "Brand" -> "https://cdn.lolchess.gg/upload/images/champions/Brand_1643374847.png"
            "Syndra" -> "https://cdn.lolchess.gg/upload/images/champions/Syndra_1643374998.png"
            "Darius" -> "https://cdn.lolchess.gg/upload/images/champions/Darius_1634784329.png"
            "Leona" -> "https://cdn.lolchess.gg/upload/images/champions/Leona_1643374919.png"
            "Tryndamere" -> "https://cdn.lolchess.gg/upload/images/champions/Tryndamere_1643375011.png"
            "Ashe" -> "https://cdn.lolchess.gg/upload/images/champions/Ashe_1643374833.png"
            "Draven" -> "https://cdn.lolchess.gg/upload/images/champions/Draven_1643374858.png"
            "Talon" -> "https://cdn.lolchess.gg/upload/images/champions/Talon_1643375005.png"
            "Sivir" -> "https://cdn.lolchess.gg/upload/images/champions/Sivir_1643374987.png"
            "JarvanIV" -> "https://cdn.lolchess.gg/upload/images/champions/JarvanIV_1643374909.png"
            "Sejuani" -> "https://cdn.lolchess.gg/upload/images/champions/Sejuani_1643374954.png"
            "Veigar" -> "https://cdn.lolchess.gg/upload/images/champions/Veigar_1643779403.png"
            "Silco" -> "https://cdn.lolchess.gg/upload/images/champions/Silco_1643374772.png"
            "Senna" -> "https://cdn.lolchess.gg/upload/images/champions/Senna_1643374978.png"
            "Caitlyn" -> "https://cdn.lolchess.gg/upload/images/champions/Caitlyn_1635139772.png"
            "MissFortune" -> "https://cdn.lolchess.gg/upload/images/champions/MissFortune_1634784448.png"
            "Zeri" -> "https://cdn.lolchess.gg/upload/images/champions/Zeri_1643374799.png"
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
                "2190"->"https://cdn.lolchess.gg/upload/images/items/ElderwoodHeirloom_1634786467.png" //돌연변이
                "89"->"https://cdn.lolchess.gg/upload/images/items/YoumuusGhostblade_1634786635.png" //암살자
                "78"->"https://cdn.lolchess.gg/upload/images/items/WarlordsBanner_1634786621.png" //화학공학
                "79"->"https://cdn.lolchess.gg/images/tft/item/Backhand.png" //덫발톱
                "47"->"https://cdn.lolchess.gg/upload/images/items/Redemption_1642015166.png" //구원
                "19"->"https://cdn.lolchess.gg/upload/images/items/InfinityEdge_ljmJbkViyMGC8IKr50os4jC8Ccl1ro2JbqXuvHqT.png" //무한의대검
                "16"->"https://cdn.lolchess.gg/upload/images/items/Bloodthirster_1640059177.png" //피바라기
                "29"->"https://cdn.lolchess.gg/upload/images/items/LastWhisper_1642015257.png" //최후의속삭임
                "55"->"https://cdn.lolchess.gg/upload/images/items/BrambleVest_1640058438.png" //덤불조끼
                "99"->"https://cdn.lolchess.gg/upload/images/items/ThiefsGloves_9PTkdu1Iyw6L1voQbNlsP5U74TzF7suIeq5RQjbh.png" //도적의장갑
                "77"->"https://cdn.lolchess.gg/upload/images/items/3083_C5Y1EvfU08Ug9wCaCJTEp2hNRjh5zSkq9eoY97Uc.png" //워모그
                "44"->"https://cdn.lolchess.gg/images/tft/item/BlueBuff.png" //블루
                "5"->"https://cdn.lolchess.gg/upload/images/items/ChainVest_1640058945.png" //사슬갑옷
                "67"->"https://cdn.lolchess.gg/images/tft/item/Zephyr.png" //서풍
                "39"->"https://cdn.lolchess.gg/images/tft/item/ArcaneGauntlet.png" //보건
                "46"->"https://cdn.lolchess.gg/upload/images/items/Chalice_D96IpanZVyS5SqA0PxLDfDp7FVAV7WGz6iCZaVLC.png" //성배
                "13"->"https://cdn.lolchess.gg/upload/images/items/HextechGunblade_1640058876.png" //총검
                "12"->"https://cdn.lolchess.gg/images/tft/item/GiantSlayer.png" //거학
                "24"->"https://cdn.lolchess.gg/upload/images/items/StatikkShiv_1640058978.png" //스태틱
                "36"->"https://cdn.lolchess.gg/upload/images/items/IonicSpark_1640059091.png" //이온충격기
                "25"->"https://cdn.lolchess.gg/upload/images/items/TitansResolve_1640058512.png" //거인의결의
                "69"->"https://cdn.lolchess.gg/images/tft/item/Quicksilver.png" //수은
                "23"->"https://cdn.lolchess.gg/upload/images/items/GuinsoosRageblade_1642015181.png" //구인수
                "66"->"https://cdn.lolchess.gg/upload/images/items/DragonsClaw_1640058416.png" //용의발톱
                "14"->"https://cdn.lolchess.gg/upload/images/items/SpearofShojin_1642015208.png" //쇼진
                "37"->"https://cdn.lolchess.gg/upload/images/items/Morellonomicon_1640058393.png" //모렐로노미콘
                "4"->"https://cdn.lolchess.gg/upload/images/items/Tearofthegoddess_1640059037.png" //여눈
                "49"->"https://cdn.lolchess.gg/images/tft/item/HandofJustice.png" //정손
                "56"->"https://cdn.lolchess.gg/upload/images/items/IronWill_AnyIyxtEcc5fby5mgL1NNIwzm76wiG0yxwcPQ7nj.png" //가고일의돌갑옷
                "22"->"https://cdn.lolchess.gg/upload/images/items/3094_SZmtvKzywMHMFOPSyWM1jysci2VFB13EmWzpUkh1.png" //고속연사포
                "17"->"https://cdn.lolchess.gg/upload/images/items/ZekesHerald_1640059135.png" //지크
                "45"->"https://cdn.lolchess.gg/upload/images/items/3110_v0619r67zsjsowMlFniZvXGsVNfdNDwoYbeCcVFX.png" //얼어붙은심장
                "88"->"https://cdn.lolchess.gg/upload/images/items/ForceofNature_1640059110.png" //전략가의왕관
                "26"->"https://cdn.lolchess.gg/upload/images/items/RunaansHurricane_1640058427.png" //루난
                "33"->"https://cdn.lolchess.gg/upload/images/items/RabadonsDeathcap_1640058833.png" //라바돈
                "9"->"https://cdn.lolchess.gg/upload/images/items/SparringGloves_1640059055.png" //연습용장갑
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
