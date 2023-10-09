<template>
  <div class="home" v-cloak>
    <div class="player1_area">
      <div class="role1">
        <el-avatar :size="100" :src="advatorUrl1" :shape="'square'" />
      </div>
      <div class="bg"></div>
      <div class="status">
        {{ statusList[ready1] }}
      </div>
      <div class="money"></div>
      <div class="point">${{ money1 }}</div>
      <div class="dice-box">
        <div class="box" v-for="(item, index) in selectArea1" :key="index">
          <div v-for="(item2, index2) in imgUrl" :key="index2">
            <img v-show="selectArea1[index] == index2" :src="item2.img" />
          </div>
        </div>
      </div>
    </div>
    <!-- 骰子区域 -->
    <div class="dice">
      <div class="dice-box">
        <div class="box" v-for="(item, index) in player1" :key="index">
          <div v-for="(item2, index2) in imgUrl" :key="index2">
            <img v-show="player1[index] == index2" :src="item2.img" />
          </div>
        </div>
      </div>
      <div class="leftInfo">
        <div class="backButton" @click="outGame"></div>
        <div class="countDown">{{ countDown }}</div>
        <div class="music">
          <!--音乐开始controls-->
          <audio id="audio" :src="bgmUrl"></audio>
          <!--音乐结束-->
          <span @click="stopMusic()" v-if="stopBackgroundMusic">
            <img src="@/assets/sound.png"
          /></span>
          <span @click="playMusic()" v-if="!stopBackgroundMusic"
            ><img src="@/assets/noSound.png"
          /></span>
        </div>
      </div>

      <div class="gameInfo">
        <div class="gameCount">第{{ gameCount }}局</div>
        <div class="term">当前第{{ round }}回合</div>
        <div class="multiple">当前倍数 {{ multiple }}!</div>
      </div>

      <div class="dice-box" style="margin-top: 5vh">
        <div
          class="box"
          :class="{ beforeStart: ready2 == 2 }"
          v-for="(item, index) in player2"
          :key="index"
          @click="selectDice(2, index)"
        >
          <div v-for="(item2, index2) in imgUrl" :key="index2">
            <img v-show="player2[index] == index2" :src="item2.img" />
          </div>
        </div>
      </div>
    </div>
    <div class="player2_area">
      <div class="role2">
        <el-avatar :size="100" :src="advatorUrl2" :shape="'square'" />
      </div>
      <div class="bg"></div>
      <div class="choose" v-if="isSure2 != 0" @click="readyForGame(2)">
        {{ buttonInfo[isSure2 - 1] }}
      </div>
      <div
        class="lock"
        :class="{ beforeStart: ready2 == 2 }"
        @click="lockDice(2)"
      ></div>
      <div class="status">
        {{ statusList[ready2] }}
      </div>
      <div
        class="plus"
        :class="{ beforeStart: isPlus2 == 0 }"
        @click="chooseIsPlus(2)"
      ></div>
      <div class="money"></div>
      <div class="point">${{ money2 }}</div>
      <div class="dice-box">
        <div class="box" v-for="(item, index) in selectArea2" :key="index">
          <div v-for="(item2, index2) in imgUrl" :key="index2">
            <img v-show="selectArea2[index] == index2" :src="item2.img" />
          </div>
        </div>
      </div>
      <div class="jetton2" v-if="isPlus2 == 1">
        <div class="number" @click="choosePlus(1)">一倍</div>
        <div class="number" @click="choosePlus(2)">两倍</div>
        <div class="number" @click="choosePlus(3)">三倍</div>
        <div class="number" @click="choosePlus(4)">取消</div>
      </div>
    </div>
  </div>

  <el-dialog
    v-model="dialogVisible"
    width="30%"
    :show-close="false"
    :style="{
      height: '76vh',
      width: '98vh',
      bottom: '18vh',
      right: '7vh',
      backgroundColor: 'rgba(0,0,0,0)',
      backgroundSize: cover,
    }"
  >
    <div class="endMsg">
      <div v-if="score1 < score2" class="win"></div>
      <div v-if="score1 > score2" class="lose"></div>
      <div v-if="score1 == score2" class="dogfall"></div>
      <div class="scorexx">{{ score2 }}</div>
      <div v-if="score1 != score2" class="moneyxx">{{ moneyxx }}</div>
      <div class="cardGroup">
        <div class="dice-box">
          <div class="box" v-for="(item, index) in selectArea2" :key="index">
            <div v-for="(item2, index2) in imgUrl" :key="index2">
              <img v-show="selectArea2[index] == index2" :src="item2.img" />
            </div>
          </div>
        </div>
      </div>
      <div class="againGame" @click="again"></div>
      <div class="outout" @click="outGame"></div>
    </div>
  </el-dialog>
  <el-dialog
    v-model="isMatch"
    width="30%"
    :show-close="false"
    :style="{
      top: '10%',
      width: '77vh',
      height: '35vh',
      backgroundColor: 'rgba(0,0,0,0)',
      radius: '10px',
      backgroundSize: cover,
    }"
  >
    <template #footer>
      <div class="matching">
        <div class="outButton" @click="outMatch"></div>
      </div>
    </template>
  </el-dialog>
  <el-dialog
    v-model="gameError"
    width="30%"
    :show-close="false"
    :style="{
      top: '10%',
      width: '77vh',
      height: '35vh',
      backgroundColor: 'rgba(0,0,0,0)',
      radius: '10px',
      backgroundSize: cover,
    }"
  >
    <template #footer>
      <div class="gameErupt">
        <div class="outButton" @click="outMatch"></div>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onBeforeUnmount, onBeforeMount, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import connectApi from "@/api/match/connect";
import disconnectApi from "@/api/match/disconnect";
import { ElMessage } from "element-plus";
import getScoreApi from "@/api/socre/getScore";
const router = useRouter();

const imgUrl = [
  {
    id: 1,
    img: require("@/assets/1.png"),
  },
  {
    id: 2,
    img: require("@/assets/2.png"),
  },
  {
    id: 3,
    img: require("@/assets/3.png"),
  },
  {
    id: 4,
    img: require("@/assets/4.png"),
  },
  {
    id: 5,
    img: require("@/assets/5.png"),
  },
  {
    id: 6,
    img: require("@/assets/6.png"),
  },
];
const advatorUrl1 = ref(
  "https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/role1.png"
);
const advatorUrl2 = ref(
  "https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/role2.png"
);
const bgmUrl = "https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/bgm.mp3";
const player1 = ref([5, 5, 5, 5, 5]);
const player2 = ref([5, 5, 5, 5, 5]);
const moneyxx = ref(0);
const player3 = reactive({
  isReady: false,
  arr: [],
});

const selectArea1 = ref([]);
const selectArea2 = ref([]);
const countDown = ref(0);
const dialogVisible = ref(false);

const setInter1 = ref(); // 定时器
const mask = ref(false);
const showNa = ref(true); //防抖
const startName = ref("即将开始 ");
const round = ref(0);
const statusList = ["未准备", "已准备", "未选定骰子", "已选定骰子"];
const buttonInfo = ["准备", "回合结束"];
const money1 = ref(1000);
const money2 = ref(localStorage.getItem("point"));
const isPlus1 = ref(2);
const isPlus2 = ref(2);
const gameCount = ref(1);

const isSure1 = ref(1);
const isSure2 = ref(1);
const ready1 = ref(0);
const ready2 = ref(0);
const score1 = ref(999);
const score2 = ref(999);
const isMatch = ref(true);
const gameError = ref(false);
const player1Phone = ref("xxx");
const player2Phone = ref(localStorage.getItem("phone"));
const isComplete = ref(false);
const idForm = reactive({
  id1: "",
  id2: "",
});
const multiple = ref(1);
const value2 = ref(0);
const msg1 = reactive({
  type: 1,
  phone: localStorage.getItem("phone"),
  money: localStorage.getItem("point"),
});
const msg2 = reactive({
  type: 4,
  index: 0,
});
const msg3 = reactive({
  type: 6,
  dice: [],
});
const msg4 = reactive({
  type: 5,
  value: 0,
});
const msg5 = reactive({
  type: 7,
  value: 0,
});
const msg6 = reactive({
  type: 8,
});

let socket = null;
const stopBackgroundMusic = ref(false);
//循环播放音乐
function loopPlayMusic() {
  var audio = document.getElementById("audio");
  audio.play();
  // 循环播放
  audio.addEventListener(
    "ended",
    function () {
      audio.play();
    },
    false
  );
}
// 播放音乐
function playMusic() {
  stopBackgroundMusic.value = true;
  loopPlayMusic();
}
// 停止播放音乐
function stopMusic() {
  var audio = document.getElementById("audio");
  audio.pause();
  stopBackgroundMusic.value = false;
}
//页面挂载时播放音乐
onMounted(() => {
  playMusic();
});
onBeforeMount(() => {
  connectApi().then((res) => {
    const data = res.data.data;
    if (res.data.status == 200) {
      if (data.isConnect) {
        isMatch.value = false;
        let temp = advatorUrl1.value;
        advatorUrl1.value = advatorUrl2.value;
        advatorUrl2.value = temp;

        console.log("进入房间");
        socket = new WebSocket(
          "wss://rrewuq.com/websocketServer/" +
            data.roomId +
            "/" +
            localStorage.getItem("userId")
        );

        socket.onopen = function () {
          socket.send(JSON.stringify(msg1));
        };
      } else {
        console.log("创建房间");
        socket = new WebSocket(
          "wss://rrewuq.com/websocketServer/" +
            localStorage.getItem("userId") +
            "/" +
            localStorage.getItem("userId")
        );
      }
      socket.onmessage = (event) => {
        if (event.data != "链接成功" && event.data != "链接断开") {
          let socketMsg = JSON.parse(event.data);

          if (socketMsg.type == 1) {
            player1Phone.value = socketMsg.phone;
            money1.value = socketMsg.money;
            isMatch.value = false;
            msg1.type = 2;
            socket.send(JSON.stringify(msg1));
          } else if (socketMsg.type == 2) {
            player1Phone.value = socketMsg.phone;
            money1.value = parseInt(socketMsg.money);
          } else if (socketMsg.type == 3) {
            gameError.value = true;
          } else if (socketMsg.type == 4) {
            selectDice(1, socketMsg.index);
          } else if (socketMsg.type == 5) {
            if (round.value != 3) {
              readyForGame(1);
              multiple.value += socketMsg.value;
            } else {
              round.value = 0;
            }
          } else if (socketMsg.type == 6) {
            if (isComplete.value) {
              player1.value = socketMsg.dice;
              isComplete.value = false;
            } else {
              player3.isReady = true;
              player3.arr = socketMsg.dice;
            }
          } else if (socketMsg.type == 7) {
            multiple.value += socketMsg.value;
            ElMessage({
              message: "对方选择加" + socketMsg.value + "倍",
              type: "success",
            });
          } else if (socketMsg.type == 8) {
            ready1.value = 3;
            ElMessage({
              message: "对方已锁定骰子",
              type: "success",
            });
          }
        }
      };
    }
  });
});
function readyForGame(player) {
  if (player == 1) {
    if (ready1.value != 3) {
      ready1.value++;
    }
    isSure1.value = 0;
  } else {
    if (ready2.value != 3) {
      ready2.value++;
    }
    isSure2.value = 0;
    msg4.value = value2.value;
    socket.send(JSON.stringify(msg4));
    multiple.value += value2.value;
    value2.value = 0;
  }
  if (isSure2.value == 0 && isSure1.value == 0) {
    countDown.value = 3;
    countDownForGame();
  }
}
function selectDice(option, index) {
  if (round.value == 0) return;
  if (option == 1 && ready1.value == 2) {
    selectArea1.value.push(player1.value[index]);
    player1.value.splice(index, 1);
  }
  if (option == 2 && ready2.value == 2) {
    selectArea2.value.push(player2.value[index]);
    player2.value.splice(index, 1);
    msg2.index = index;
    socket.send(JSON.stringify(msg2));
  }
}
// 倒计时3秒
function countDownForGame() {
  let timer = setInterval(() => {
    countDown.value -= 1;
    if (countDown.value == 0) {
      clearInterval(timer);
      handlestart();
    }
  }, 1000);
}
function outGame() {
  if (socket != null) {
    msg1.type = 3;
    socket.send(JSON.stringify(msg1));
    socket.close();
  }
  router.push("/");
}
function lockDice() {
  if (ready2.value == 2) {
    ready2.value = 3;
    socket.send(JSON.stringify(msg6));
  }
}
function chooseIsPlus(option) {
  if (isPlus2.value == 0) {
    isPlus2.value = 1;
  }
}
function choosePlus(number) {
  if (number == 4) {
    isPlus2.value = 0;
  } else {
    isPlus2.value = 2;
    multiple.value += number;
    msg5.value = number;
    console.log("number=");
    console.log(msg5.value);
    socket.send(JSON.stringify(msg5));
  }
}
function outMatch() {
  disconnectApi();
  if (socket != null) {
    socket.close();
  }
  router.push("/");
}
function again() {
  dialogVisible.value = false;
  for (var q = 0; q < 5; q++) {
    player1.value.push(5);
    player2.value.push(5);
  }
  selectArea1.value.splice(0, 5);
  selectArea2.value.splice(0, 5);
  isSure1.value = 1;

  ready1.value = 0;
  ready2.value = 0;
  isSure2.value = 1;
  multiple.value = 1;
  gameCount.value++;
  if (round.value != 0) {
    round.value = 0;
  } else {
    readyForGame(1);
  }
}
function handlestart() {
  //开始
  //showNa.value = false; //防抖
  setInter1.value = setInterval(() => {
    for (var i = 0; i < player1.value.length; i++) {
      player1.value[i]++;
    }
    for (var l = 0; l < player2.value.length; l++) {
      player2.value[l]++;
    }
    if (player1.value[0] == imgUrl.length) {
      for (var j = 0; j < player1.value.length; j++) {
        player1.value[j] = 0;
      }
    }
    if (player2.value[0] == imgUrl.length) {
      for (var k = 0; k < player2.value.length; k++) {
        player2.value[k] = 0;
      }
    }
  });
  setTimeout(() => {
    clearInterval(setInter1.value); // 先将已有的计时器清除
    //随机1-6点数
    for (var j = 0; j < player2.value.length; j++) {
      player2.value[j] = Math.round(Math.random() * 5);
    }
    setTimeout(() => {
      showNa.value = true;
      mask.value = true;
    }, 300);
    isComplete.value = true;
    round.value++;
    msg3.dice = player2.value;
    socket.send(JSON.stringify(msg3));
    if (player3.isReady) {
      player1.value = player3.arr;
      player3.isReady = false;
      isComplete.value = false;
    }

    setTimeout(() => {
      if (round.value == 3) {
        for (var p = player1.value.length - 1; p >= 0; p--) {
          selectArea1.value.push(player1.value[p]);
          player1.value.splice(p, 1);
        }
        for (var w = player2.value.length - 1; w >= 0; w--) {
          selectArea2.value.push(player2.value[w]);
          player2.value.splice(w, 1);
        }
        let arr1 = [];
        let arr2 = [];
        for (let w = 0; w < 5; w++) {
          arr1.push(selectArea1.value[w] + 1);
          arr2.push(selectArea2.value[w] + 1);
        }
        arr1.sort((a, b) => a - b);
        arr2.sort((a, b) => a - b);
        idForm.id1 = arr1.join("");
        idForm.id2 = arr2.join("");
        setTimeout(() => {
          getScoreApi(idForm).then((res) => {
            score1.value = JSON.parse(res.data.score1).score;
            score2.value = JSON.parse(res.data.score2).score;

            let score = score1.value - score2.value;
            score *= multiple.value;
            moneyxx.value = Math.abs(score);
            if (money1.value + score < 0) {
              money2.value += money1.value;
              money1.value = 0;
            } else if (money2.value - score < 0) {
              money1.value += money2.value;
              money2.value = 0;
            } else {
              if (score > 0) {
                score = parseInt(Math.abs(score));
                money1.value = parseInt(money1.value) + parseInt(score);
                money2.value -= parseInt(score);
              } else {
                score = parseInt(Math.abs(score));
                money1.value -= parseInt(score);
                money2.value = parseInt(money2.value) + parseInt(score);
              }
              dialogVisible.value = true;
            }
          });
        }, 50);
      } else {
        ready1.value = 2;
        ready2.value = 2;
        isSure1.value = 2;
        isSure2.value = 2;
        isPlus1.value = 0;
        isPlus2.value = 0;
      }
    }, 500);
  }, 1000);
}
onBeforeUnmount(() => {
  if (socket != null) {
    msg1.type = 3;
    socket.send(JSON.stringify(msg1));
    socket.close();
  }
});
</script>
<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
}
* {
  /* CSS Reset */
  margin: 0;
  padding: 0;
}
.home {
  overflow: hidden;
  position: fixed;
  height: 100%; //高
  width: 100%; //宽
  top: 0px; //缩短白边距离
  left: 0px; //缩短白边距离
  // 设置字体STHupo
  font-family: STHupo;
  font-size: 2vh;

  background-image: url("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/background.png");
  background-size: cover;
}
.endMsg {
  width: 98vh;
  height: 90vh;
}
.scorexx {
  width: 8vh;
  height: 6vh;
  background-image: url("../assets/rect.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  font-size: 3vh;
  color: rgb(239, 243, 23);
  top: 39%;
  right: 10%;
  text-align: center;
  font-family: STHupo;
  line-height: 6vh;
}
.moneyxx {
  width: 8vh;
  height: 6vh;
  background-image: url("../assets/rect.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  font-size: 3vh;
  top: 49%;
  color: rgb(239, 243, 23);
  right: 10%;
  font-family: STHupo;
  text-align: center;
  line-height: 6vh;
}
.cardGroup {
  width: 42vh;
  height: 10vh;
  background-image: url("../assets/rect.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  bottom: 18%;
  right: 1%;
  .dice-box {
    height: 10vh;
    width: 42vh;
    .box {
      width: 7.8vh;
      height: 9vh;
      position: relative;
      top: 0.5vh;
      img {
        width: 7.8vh;
        height: 9vh;
      }
    }
  }
}
.againGame {
  width: 18vh;
  height: 6vh;
  background-image: url("../assets/again.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  bottom: 5%;
  left: 20%;
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }

  cursor: pointer;
}
.outout {
  width: 18vh;
  height: 6vh;
  background-image: url("../assets/outout.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  bottom: 5%;
  right: 20%;
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }

  cursor: pointer;
}
.win {
  width: 98vh;
  height: 70vh;
  background-image: url("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/win.png");
  background-size: cover;
  background-size: 100% 100%;
}
.lose {
  width: 98vh;
  height: 70vh;
  background-image: url("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/lose.png");
  background-size: cover;
  background-size: 100% 100%;
}
.dogfall {
  width: 98vh;
  height: 70vh;
  background-image: url("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/dogfall.png");
  background-size: cover;
  background-size: 100% 100%;
}
[v-cloak] {
  display: none !important;
}
.beforeStart {
  // 当鼠标在上面时，框变大动画效果
  &:hover {
    transform: scale(1.07);
    transition: all 0.3s;
  }
  cursor: pointer;
}
.choose {
  width: 18vh;
  height: 6vh;
  background-image: url("../assets/option.png");
  background-size: cover;
  line-height: 6vh;
  color: rgb(219, 220, 161);
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }

  cursor: pointer;
}

.dice {
  position: absolute;
  top: 28.8vh;
  width: 100%;
  display: flex;
  flex-flow: column;
  .dice-box {
    margin-left: 48vh;
  }
}
.dice-box {
  width: 85vh;
  height: 18vh;
  display: flex;
  justify-content: space-evenly;

  .box {
    width: 12.5vh;
    height: 17.4vh;
    position: relative;
    top: 0.3vh;

    img {
      width: 12.5vh;
      height: 17.4vh;
    }
    div:nth-child(2) {
      margin-left: 8px;
    }

    div:nth-child(4) {
      margin-left: -2px;
    }

    div:nth-child(5) {
      margin-right: 5px;
    }
  }
}
:deep .el-avatar {
  height: 27vh;
  width: 20.342vh;
}
.role1 {
  position: absolute;
  top: 0%;
  right: 1%;
}
.role2 {
  position: absolute;
  bottom: 0%;
  left: 1%;
}
.info {
  margin: 20px auto 0;
  span {
    display: block;
    font-size: 18px;
  }
}
.el-avatar {
  size: 100px;
}
.outButton {
  width: 15vh;
  height: 5vh;

  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  bottom: 0%;
  right: 7.3%;
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }

  cursor: pointer;
}
.matching {
  width: 72vh;
  height: 27vh;
  background-image: url("../assets/matching.png");
  background-size: cover;
  background-size: 100% 100%;
  .outButton {
    background-image: url("../assets/outMatch.png");
  }
}
.gameErupt {
  width: 72vh;
  height: 27vh;
  background-image: url("../assets/outGame.png");
  background-size: cover;
  background-size: 100% 100%;
  .outButton {
    background-image: url("../assets/return.png");
  }
}

.lock {
  position: absolute;
  height: 4.2vh; //高
  width: 12vh; //宽
  background-size: cover;

  background-image: url("../assets/lock.png");
}
.plus {
  position: absolute;
  height: 4.2vh; //高
  width: 12vh; //宽
  background-image: url("../assets/plus.png");
  background-size: cover;
}
.money {
  position: absolute;
  height: 14vh; //高
  width: 15vh; //宽
  background-image: url("../assets/money.png");
  background-size: cover;
}
.number {
  width: 18vh;
  height: 6vh;
  font-size: 2vh;
  margin-left: 2.5vh;
  background-image: url("../assets/option.png");
  background-size: cover;
  line-height: 6vh;
  color: rgb(219, 220, 161);
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }

  cursor: pointer;
}
.point {
  position: absolute;
  height: 11vh; //高
  width: 16vh; //宽
  background-image: url("../assets/point.png");
  background-size: cover;
  line-height: 11vh;
}
.status {
  position: absolute;
  height: 11vh; //高
  width: 16vh; //宽
  background-image: url("../assets/point.png");
  background-size: cover;
  text-align: center;
  line-height: 11vh;
}
.player2_area {
  height: 28vh;
  width: 100%;
  position: absolute;
  bottom: 0;
  .money {
    top: 6vh;
    right: 5%;
  }
  .point {
    top: 18vh;
    right: 5%;
  }
  .lock {
    top: 10vh;
    left: 15%;
  }
  .jetton2 {
    position: absolute;
    top: 0vh;
    left: 26%;
    display: flex;
  }
  .plus {
    top: 15vh;
    left: 15%;
  }
  .status {
    bottom: 0vh;
    left: 13.8%;
  }
  .choose {
    position: absolute;
    top: 0vh;
    left: 13.2%;
  }
  .bg {
    position: absolute;
    bottom: 0%;
    height: 22vh;
    width: 100%;
    background-color: rgb(9, 9, 9);
    opacity: 0.5;
    z-index: -1;
  }
  .dice-box {
    position: absolute;
    right: 18%;
    bottom: 0%;
    height: 20vh;
    width: 100vh;
    .box {
      width: 13.4vh;
      height: 19vh;
      position: relative;
      top: 0vh;
      img {
        width: 13.4vh;
        height: 19vh;
      }
    }
  }
}
.player1_area {
  height: 28vh;
  width: 100%;
  position: absolute;
  top: 0;
  .money {
    bottom: 8vh;
    left: 5%;
  }
  .point {
    bottom: 18vh;
    left: 5%;
  }
  .jetton1 {
    position: absolute;
    bottom: 0vh;
    right: 28%;
    display: flex;
  }
  .lock {
    bottom: 10vh;
    right: 15%;
  }
  .plus {
    bottom: 15vh;
    right: 15%;
  }
  .status {
    right: 13.8%;
  }
  .choose {
    position: absolute;
    bottom: 0vh;
    right: 13.2%;
  }

  .bg {
    position: absolute;
    top: 0%;
    height: 22vh;
    width: 100%;
    background-color: rgb(9, 9, 9);
    opacity: 0.5;
    z-index: -1;
  }
  .dice-box {
    position: absolute;
    left: 18%;
    top: 0%;
    height: 20vh;
    width: 100vh;
    .box {
      width: 13.4vh;
      height: 19vh;
      position: relative;
      img {
        width: 13.4vh;
        height: 19vh;
      }
    }
  }
}
.gameInfo {
  position: absolute;
  width: 28vh;
  right: 5vh;
  top: 11vh;
  justify-content: space-between;
  .gameCount {
    width: 22vh;
    height: 5.5vh;
    background-image: url("../assets/info.png");
    background-size: cover;
    text-align: center;
    line-height: 5.5vh;
  }
  .term {
    margin-top: 5vh;
    width: 22vh;
    height: 5.5vh;
    background-image: url("../assets/info.png");
    background-size: cover;
    text-align: center;
    line-height: 5.5vh;
  }
  .multiple {
    margin-top: 5vh;
    width: 22vh;
    height: 5.5vh;
    background-image: url("../assets/info.png");
    background-size: cover;
    text-align: center;
    line-height: 5.5vh;
  }
}
.leftInfo {
  position: absolute;
  width: 28vh;
  left: 5vh;
  top: 1vh;
  justify-content: space-between;
  .backButton {
    width: 8.2vh;
    height: 7.2vh;
    background-image: url("../assets/back.png");
    background-size: cover;
    &:hover {
      transform: scale(1.07);
      transition: all 0.3s;
    }
    cursor: pointer;
  }
  .music {
    position: absolute;
    top: 0%;
    left: 11vh;
    width: 8.2vh;
    height: 7.2vh;
    &:hover {
      transform: scale(1.07);
      transition: all 0.3s;
    }
    cursor: pointer;
    .div {
      width: 8.2vh;
      height: 7.2vh;
      .image {
        width: 8.2vh;
        height: 7.2vh;
      }
    }
  }
  .countDown {
    position: absolute;
    top: 17vh;
    right: 2vh;
    width: 12.8vh;
    height: 8.2vh;
    background-image: url("../assets/countDown.png");
    background-size: cover;
    text-align: center;
    line-height: 8.2vh;
    font-size: 3.5vh;
  }
}
</style>