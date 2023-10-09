<template>
  <div class="common-layout">
    <el-container>
      <el-header height="90px">
        <span
          style="
            font-size: 40px;
            font-weight: bold;
            color: #409eff;
            margin-top: 40px;
          "
          >日本核污染水排海视频弹幕分析挖掘</span
        >
      </el-header>
    </el-container>
  </div>
  <el-row style="height: 770px">
    <el-col :span="2"> </el-col>
    <el-col
      :span="20"
      style="display: flex; align-items: center; justify-content: center"
    >
      <div id="barrageData" style="width: 900px; height: 700px"></div>

      <div class="wordCloud">
        <img src="@/assets/a.jpg" class="cloudP" />
      </div>
      <div
        style="
          width: 100px;
          height: 30px;
          position: absolute;
          top: 780px;
          right: 365px;
          font-size: 20px;
          font-weight: bold;
          color: #409eff;
          border-radius: 8px;
          border: 1px solid rgb(226, 230, 237);
          background-color: rgb(248, 249, 250);
          cursor: pointer;
        "
      >
        <el-button native-type="submit" type="primary" @click="exportExcel">
          导出数据
        </el-button>
      </div>
    </el-col>
    <el-col :span="2"> </el-col>
  </el-row>
</template>

<script setup>
import getBarrageApi from "@/api/barrage/getBarrage";

import { reactive, onMounted } from "vue";
import { export_json_to_excel } from "@/utils/excel";
//import { useRouter } from "vue-router";
import * as echarts from "echarts";
onMounted(() => {
  getBarrageApi().then((res) => {
    if (res.data.code == 200) {
      barrageData.barrageName = res.data.data.barrageName;
      barrageData.barrageCount = res.data.data.barrageCount;
      showBarrageData();
    }
  });
});

const barrageData = reactive({
  barrageName: [],
  barrageCount: [],
});

function showBarrageData() {
  const chartBox = echarts.init(document.getElementById("barrageData")); // 主要
  const option = {
    title: {
      text: "数据统计",
      bottom: 1,
      left: "center",
    },
    xAxis: {
      data: barrageData.barrageName,
      axisLabel: {
        formatter: function (value) {
          if (value.length > 7) {
            return value.substr(0, 6) + "...";
          } else {
            return value;
          }
        },
        rotate: -45, // 设置旋转角度为 45 度
      },
      name: "弹幕",
    },

    yAxis: {
      name: "数量",
    },
    series: [
      {
        type: "bar",
        data: barrageData.barrageCount,
      },
    ],
  };
  chartBox.setOption(option);
  // 根据页面大小自动响应图表大小
  window.addEventListener("resize", function () {
    chartBox.resize();
  });
}

function exportExcel() {
  let headName = ["排名", "弹幕", "数量"];

  let dataList = [];
  for (let i = 0; i < 20; i++) {
    let arr = [];
    arr.push(i + 1);
    arr.push(barrageData.barrageName[i]);
    arr.push(barrageData.barrageCount[i]);
    dataList.push(arr);
  }

  let dataExcel = {
    header: headName,
    data: dataList,
    filename: "弹幕数据汇总表",
  };
  export_json_to_excel(dataExcel);
}
</script>

<style scoped>
/* barrageData */
#barrageData {
  /* 利用定位移到左侧 */
  position: absolute;
  left: 100px;
  top: 130px;
}
/* wordCloud */
.wordCloud {
  position: absolute;
  right: 100px;
  top: 100px;
  width: 400px;
  height: 400px;
}
.cloudP {
  position: absolute;
  top: 90px;
  right: 15%;
  height: 500px;
  width: 500px;
}
</style>
