package com.newage.plantedaqua.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.newage.plantedaqua.R
import com.newage.plantedaqua.adapters.RecyclerAdapterInfo
import com.newage.plantedaqua.adapters.RecyclerAdapterLogs
import com.newage.plantedaqua.databinding.EachTankDetailLayoutBinding
import com.newage.plantedaqua.dbhelpers.MyDbHelper
import com.newage.plantedaqua.dbhelpers.TankDBHelper
import com.newage.plantedaqua.models.LogData
import com.newage.plantedaqua.models.TankAdviceInfo
import com.newage.plantedaqua.models.TanksDetails
import com.newage.plantedaqua.viewmodels.A1ViewModel
import kotlinx.android.synthetic.main.each_tank_detail_layout.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class TanksPlaceholderFragment : Fragment(),View.OnClickListener{

    val a1ViewModel by sharedViewModel<A1ViewModel>()
   // private lateinit var tanksDetails: ArrayList<TanksDetails>
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tankOptionsImage -> showTankOptions()
            R.id.resetImage -> showTankOptions()
            R.id.showDosage -> showHideDosageInfo()
        }
    }

    private val logData2 = java.util.ArrayList<LogData>()
    private val logData1 = java.util.ArrayList<LogData>()
    private lateinit var myDbHelper: MyDbHelper
    private var tankOptionsVisible = true
    private var dosageInfoVisible = false
    private lateinit var adapter2: RecyclerAdapterLogs
    private lateinit var recoAdapter : RecyclerAdapterInfo

    private lateinit var  binding : EachTankDetailLayoutBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.each_tank_detail_layout,container,false)
        binding.apply {
           tankOptionsImage.setOnClickListener(this@TanksPlaceholderFragment)
           resetImage.setOnClickListener(this@TanksPlaceholderFragment)
            showDosage.setOnClickListener(this@TanksPlaceholderFragment)
        }


        return binding.root
    }

    companion object{
       private const val TANKS_FRAGMENT_SECTION_NUMBER = "tank_section_number"
        @JvmStatic
        fun newInstance(sectionNumber : Int):TanksPlaceholderFragment{
            val tanksPlaceholderFragment = TanksPlaceholderFragment()
            val args = Bundle()
            args.putInt(TANKS_FRAGMENT_SECTION_NUMBER,sectionNumber)
            tanksPlaceholderFragment.arguments = args
            return tanksPlaceholderFragment
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {


        binding.boundTankDetails = a1ViewModel.getTankDetailsArrayList()!![requireArguments().getInt(TANKS_FRAGMENT_SECTION_NUMBER)]
        Glide.with(this)
                .load(binding.boundTankDetails!!.tankPicUri)
                .placeholder(R.drawable.aquarium2)
                .into(binding.imageView)

        myDbHelper = MyDbHelper.newInstance(activity, binding.boundTankDetails!!.tankID)


        addDosageText()
        upcomingTasks()
        pendingTasks()
        setReco()
        binding.scrollView2.smoothScrollTo(0,0)
        super.onActivityCreated(savedInstanceState)
    }

    //region ANIMATIONS
    private fun showHideDosageInfo() {

        binding.apply {
            showDosage.animate().rotationBy(180f).duration = 500L

            if (dosageInfoVisible) {

                dosageInfoLayout.animate().translationY(-100f).alpha(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        dosageInfoLayout.visibility = View.GONE
                    }
                })




            } else {
                dosageInfoLayout.translationY = -100f
                dosageInfoLayout.alpha = 0f
                dosageInfoLayout.visibility = View.VISIBLE
                dosageInfoLayout.animate().translationY(0f).alpha(1f).setDuration(500L).setListener(null)


            }
            dosageInfoVisible = !dosageInfoVisible

        }
    }


    private fun showTankOptions(){

        binding.apply {



            resetImage.resetImageAnimation()

            if (tankOptionsVisible) {
                scrollView2.smoothScrollTo(0, 0)
                TankNameText.animate().translationY(-100f).duration = 300L
                tankOptionsImage.fadeOutAnimation()
                deleteTank.fadeInAnimation()
                editImage.fadeInAnimation()
                constraintLayoutEachTankOptions.apply {
                    alpha = 0f
                    scaleX = 0f
                    scaleY = 0f
                    visibility = View.VISIBLE
                    animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(500L).setListener(null)

                }
            }
                 else {
                TankNameText.animate().translationY(0f).duration = 500L
                tankOptionsImage.fadeInAnimation()
                deleteTank.fadeOutAnimation()
                editImage.fadeOutAnimation()
                    constraintLayoutEachTankOptions.apply {

                        animate().alpha(0f).scaleX(0f).scaleY(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                visibility = View.GONE
                            }
                        })
                    }
                }


        }





        tankOptionsVisible=!tankOptionsVisible

    }

    //Animate reset image separately to ensure there is no overlapping during hide animation
    private fun View.resetImageAnimation(){

        if (tankOptionsVisible){

                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(500L).setListener(null)

        }
        else {

            resetImage.visibility = View.GONE
        }

    }

    private fun View.fadeAnimation(){
        if (tankOptionsVisible){
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500L).setListener(null)

        }
        else {

            animate().alpha(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter(){
                override fun onAnimationEnd(animation: Animator?) {
                    visibility = View.GONE
                }
            })



        }
    }

    private fun View.fadeInAnimation(){
        alpha = 0f
        visibility = View.VISIBLE
        animate().alpha(1f).setDuration(500L).setListener(null)
    }

    private fun View.fadeOutAnimation(){
        animate().alpha(0f).setDuration(500L).setListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                visibility = View.GONE
            }
        })
    }
    //endregion




    private fun addDosageText() {

        binding.apply {

                if (!boundTankDetails!!.microDosageText.isBlank()) {
                    DosageMicro.text = boundTankDetails!!.microDosageText
                    DosageMicro.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorAccent))
                } else DosageMicro.text = requireActivity().resources.getString(R.string.no_data_for_micro_dosage)
                if (!boundTankDetails!!.macroDosageText.isBlank()) {
                    DosageMacro.text = boundTankDetails!!.macroDosageText
                    DosageMacro.setTextColor(ContextCompat.getColor(requireActivity(),R.color.colorAccent))
                } else DosageMacro.text = requireActivity().resources.getString(R.string.no_data_for_macro_dose)
            }

    }





    //region UPDATE TASK RECYCLER VIEWS
    private fun upcomingTasks() {

        val db = myDbHelper.readableDatabase
        val c = myDbHelper.getData(db)
        if (c.moveToFirst()) {
            do {
                collectAlarmData(c.getString(0), c.getString(1), c.getString(2).toLong(), c.getString(4).toInt(), c.getString(5).toInt(), c.getString(6).toInt())
            } while (c.moveToNext())
        }
        c.close()
        Collections.sort(logData1, Comparator<LogData> { lhs, rhs -> lhs.timeInMillis.toString().compareTo(rhs.timeInMillis.toString()) })
        binding.apply {
            UpcomingDashBoardRecyclerView.viewTreeObserver
                    .addOnGlobalLayoutListener( OnViewGlobalLayoutListener(UpcomingDashBoardRecyclerView, 256, activity))
            if (logData1.isEmpty()) {
                UpcomingDashBoardTasks.text = "There are no upcoming tasks"
                LinearDashBoardUpcoming.visibility = View.GONE
            } else {
                LinearDashBoardUpcoming.visibility = View.VISIBLE
                UpcomingDashBoardTasks.text = "Upcoming Tasks"
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                UpcomingDashBoardRecyclerView.layoutManager = layoutManager
                val adapter1 = RecyclerAdapterLogs(logData1, activity, RecyclerAdapterLogs.OnItemClickListener { _, _ -> })
                UpcomingDashBoardRecyclerView.adapter = adapter1
            }
        }

    }

    private fun collectAlarmData(alarmName: String, alarmCategory: String, alarmTimeInMillis: Long, dayNumber: Int, hr: Int, min: Int) {
        val logData = LogData()
        val strDays = arrayOf(
                "Sunday",
                "Monday",
                "Tuesday",
                "Wednesday",
                "Thursday",
                "Friday",
                "Saturday"
        )
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dateString = formatter.format(Date(alarmTimeInMillis))
        val calendar = Calendar.getInstance()
        val currentTimeInMillis = calendar.timeInMillis
        if (currentTimeInMillis < alarmTimeInMillis) {
            logData.dy = setRelativeDays(alarmTimeInMillis, strDays[dayNumber - 1])
            logData.category = alarmCategory
            logData.task = alarmName
            logData.dt = dateString + " " + timeFormat(hr, min)
            logData.timeInMillis = alarmTimeInMillis
            logData1.add(logData)
        }
    }

    private fun setRelativeDays(timeInMillis: Long, defaultValue: String): String? {
        if (DateUtils.isToday(timeInMillis)) {
            return "TODAY"
        } else if (isTomorrow(timeInMillis)) {
            return "TOMORROW"
        } else if (isYesterday(timeInMillis)) {
            return "YESTERDAY"
        }
        return defaultValue
    }

    private fun timeFormat(hr: Int, min: Int): String {
        var hour = hr
        val format: String
        val strHr: String
        if (hour == 0) {
            hour += 12
            format = "AM"
        } else if (hour == 12) {
            format = "PM"
        } else if (hour > 12) {
            hour -= 12
            format = "PM"
        } else {
            format = "AM"
        }
        strHr = if (hour < 10) {
            "0$hour"
        } else {
            hour.toString()
        }
        val strMin: String = if (min < 10) {
            "0$min"
        } else {
            min.toString()
        }
        return "$strHr:$strMin $format"
    }

    private fun pendingTasks() {

        val customAlertDialog = CustomAlertDialog()

        val c = myDbHelper.getDataLogsCondition("Log_Status", "Skipped")
        if (c.moveToFirst()) {
            do {
                val logData = LogData()
                logData.category = c.getString(3)
                logData.task = c.getString(2)
                logData.dt = c.getString(1)
                if (TextUtils.isEmpty(c.getString(6))) {
                    logData.dy = c.getString(0)
                } else {
                    logData.dy = setRelativeDays(c.getString(6).toLong(), c.getString(0))
                }
                logData.status = c.getString(4)
                logData2.add(logData)
            } while (c.moveToNext())
        }
        c.close()
        binding.apply {
            PendingDashBoardRecyclerView.viewTreeObserver
                    .addOnGlobalLayoutListener( OnViewGlobalLayoutListener(UpcomingDashBoardRecyclerView, 256, activity))
            if (logData2.isEmpty()) {
                PendingDashBoardTasks.text = getString(R.string.no_pending_tasks)
                LinearDashBoardPending.visibility = View.GONE
            } else {
                LinearDashBoardPending.visibility = View.VISIBLE
                PendingDashBoardTasks.text = getString(R.string.pending_tasks)
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                PendingDashBoardRecyclerView.layoutManager = layoutManager
                adapter2 = RecyclerAdapterLogs(logData2, activity, RecyclerAdapterLogs.OnItemClickListener { view, position ->
                    val task: String = logData2[position].task
                    val dt: String = logData2[position].dt
                    if (view.tag == 1) {

                        customAlertDialog.showDialog(requireActivity(),null,"Mark Task As Completed","You can undo this action in your Tank Logs.") {
                            myDbHelper.updateItemLogsUsingDate(dt, "Log_Status", resources.getString(R.string.Completed))
                            myDbHelper.updateStatusMaL(resources.getString(R.string.Completed), dt)
                            logData2.removeAt(position)
                            adapter2.notifyItemRemoved(position)
                            if (logData2.isEmpty()) {
                                PendingDashBoardTasks.text = getString(R.string.no_pending_tasks)
                                PendingDashBoardRecyclerView.visibility = View.GONE
                                LinearDashBoardPending.visibility = View.GONE
                            } else {
                                PendingDashBoardTasks.text = getString(R.string.pending_tasks)
                                PendingDashBoardRecyclerView.visibility = View.VISIBLE
                                LinearDashBoardPending.visibility = View.VISIBLE
                            }
                            Toast.makeText(activity, "Task $task is completed..", Toast.LENGTH_SHORT).show()
                        }



                    } else {

                        customAlertDialog.showDialog(requireActivity(), null, "Delete Log", "Deleted Logs cannot be recovered.") {
                            myDbHelper.deleteItemLogsUsingDate(dt)
                            myDbHelper.deleteItemMaL(dt)
                            logData2.removeAt(position)
                            adapter2.notifyItemRemoved(position)
                            if (logData2.isEmpty()) {
                                PendingDashBoardTasks.text = getString(R.string.no_pending_tasks)
                                PendingDashBoardRecyclerView.visibility = View.GONE
                                LinearDashBoardPending.visibility = View.GONE
                            } else {
                                PendingDashBoardTasks.text = getString(R.string.pending_tasks)
                                PendingDashBoardRecyclerView.visibility = View.VISIBLE
                                LinearDashBoardPending.visibility = View.VISIBLE
                            }
                            Toast.makeText(activity, "Task $task is deleted from Logs", Toast.LENGTH_SHORT).show()
                        }
                    }


                })
                PendingDashBoardRecyclerView.adapter = adapter2
            }
        }
    }


    private fun setReco() {
        val tankAdviceInfoList = ArrayList<TankAdviceInfo>()
        var tankAdviceInfo: TankAdviceInfo

        val tankDBHelper = TankDBHelper.newInstance(activity)
        val c = tankDBHelper.getDataRecoCondition(tankDBHelper.readableDatabase, binding.boundTankDetails!!.tankID)
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    if (!TextUtils.isEmpty(c.getString(5))&&(c.getString(6) == "1")) {
                        tankAdviceInfo = TankAdviceInfo()
                        tankAdviceInfo.infoType = c.getString(4)
                        tankAdviceInfo.infoDate = c.getString(3)
                        tankAdviceInfo.infoMessage = c.getString(5)

                        tankAdviceInfoList.add(tankAdviceInfo)
                    }
                } while (c.moveToNext())
            }
            c.close()
        }

        binding.apply {
            RecoDashBoardRecyclerView.viewTreeObserver
                    .addOnGlobalLayoutListener( OnViewGlobalLayoutListener(UpcomingDashBoardRecyclerView, 256, activity))
            if (tankAdviceInfoList.isEmpty()) {
                RecommendationsDashBoard.text = resources.getString(R.string.no_reco)
                LinearRecoDashBoard.visibility = View.GONE
            } else {
                LinearRecoDashBoard.visibility = View.VISIBLE
                RecommendationsDashBoard.text = resources.getString(R.string.reco)
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)
                RecoDashBoardRecyclerView.layoutManager = layoutManager
                recoAdapter = RecyclerAdapterInfo(tankAdviceInfoList, activity, RecyclerAdapterInfo.OnItemClickListener { _, _ -> })
                RecoDashBoardRecyclerView.adapter = recoAdapter
            }
        }
    }

    private fun isYesterday(d: Long): Boolean {
        return DateUtils.isToday(d + DateUtils.DAY_IN_MILLIS)
    }

    private fun isTomorrow(d: Long): Boolean {
        return DateUtils.isToday(d - DateUtils.DAY_IN_MILLIS)
    }
    //endregion


}

class TanksSectionsPagerAdapter(private val size:Int, fm:FragmentManager): FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getCount(): Int {
        return size
    }

    override fun getItem(position: Int): Fragment {
        return TanksPlaceholderFragment.newInstance(position)
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}

