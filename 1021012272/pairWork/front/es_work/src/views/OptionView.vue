<template>
  <div class="help" @click="dialogVisible = true"></div>
  <div class="home">
    <div class="menu">
      <div class="local" @click="goLocal"></div>
      <div class="ai" @click="goAi"></div>
      <div class="people" @click="goPeople"></div>
    </div>
  </div>

  <el-dialog
    v-model="dialogVisible"
    :style="{
      top: '5vh',
      height: '55vh',
      background: 'rgba(0, 0, 0, 0.7)',
      radius: '10px',
      backgroundSize: cover,
    }"
  >
    <div class="gameMsg">
      <div class="msg">
        <div class="div1">游戏规则</div>
        <div>
          每一局游戏内有三轮投掷机会，玩家一人一次轮流投掷骰子，每个玩家有5个骰子
        </div>
        <br />
        <div>
          前两次投掷骰子之后可以选择锁定0~5个骰子，锁定的骰子位于选定区域不再改变
        </div>
        <br />
        <div>
          前两轮每轮投掷结束并在所有玩家锁定骰子后，每位玩家可以选择增加倍率：0，1，2，3。选择完成后进入下一轮。
        </div>
        <br />
        <div>
          第三轮投掷结束后，所有玩家的全部骰子自动锁定并进行计分（得分为五个骰子的点数总和+奖励分），得出最终结果，筹码划分。
        </div>
      </div>
    </div>
  </el-dialog>
  <el-dialog
    v-model="loginVisible"
    :show-close="false"
    width="30%"
    :style="{
      height: '40vh',
      width: '50vh',
      top: '5vh',
      backgroundColor: 'rgba(0,0,0,0)',
      backgroundSize: cover,
    }"
  >
    <div class="loginMsg">
      <el-form
        :label-position="labelPosition"
        label-width="100px"
        :model="formPhone"
        :style="{
          position: 'absolute',
          top: '45%',
          left: '23%',
          width: '25vh',
          height: '10vh',
        }"
      >
        <el-form-item>
          <el-input v-model="formPhone.phone" placeholder="请输入账号 " />
        </el-form-item>
        <el-form-item
          :style="{
            marginTop: '6.5vh',
          }"
        >
          <el-input
            v-model="formPhone.password"
            placeholder="请输入密码"
            type="password"
          />
        </el-form-item>
      </el-form>
      <div class="loginB" @click="phoneLogin"></div>
    </div>
  </el-dialog>
</template>



<script setup>
import { useRouter } from "vue-router";
import { ref, reactive } from "vue";
import loginApi from "@/api/login/phoneLogin";
const router = useRouter();
const dialogVisible = ref(false);
const loginVisible = ref(false);
const activeName = ref("first");
const formPhone = reactive({
  phone: "",
  password: "",
});
const isLogin = ref(false);
function goLocal() {
  router.push("/game/local");
}
function goAi() {
  router.push("/game/ai");
}
function phoneLogin() {
  loginApi(formPhone).then((res) => {
    const data = res.data.data;
    if (res.data.status == 200) {
      localStorage.setItem("token", data.tokenValue);
      localStorage.setItem("userId", data.user.id);
      localStorage.setItem("phone", data.user.phone);
      localStorage.setItem("point", data.user.point);
      router.push("/game/people");
    }
  });
}
function goPeople() {
  if (!isLogin.value) {
    loginVisible.value = true;
  }
}
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
  z-index: -1;
  background-image: url("https://bucketofpicture.oss-cn-hangzhou.aliyuncs.com/desktop.png");
  background-size: cover;
}
.menu {
  justify-content: space-around;
  align-items: center;
  height: 45vh;

  flex-flow: column;
  position: absolute;
  top: 48%;
  left: 39%;

  div {
    width: 39vh;
    height: 9vh;
    background-size: 100% 100%;
    margin-top: 4vh;
    &:hover {
      transform: scale(1.04);
      transition: all 0.2s;
    }

    cursor: pointer;
  }
  .local {
    background-image: url("../assets/vsLocal.png");
  }
  .ai {
    background-image: url("../assets/vsAI.png");
  }
  .people {
    background-image: url("../assets/vsPeople.png");
  }
}
.help {
  height: 7.5vh;
  width: 7.5vh;
  position: absolute;
  top: 2%;
  right: 2%;
  cursor: pointer;
  border-radius: 5px;
  background-image: url("../assets/help.png");
  background-size: cover;

  &:hover {
    transform: scale(1.07);
    transition: all 0.3s;
  }
}

.msgDialog {
  background-color: #000;
  background-size: cover;
  height: 50vh;
}
.gameMsg {
  .msg {
    .div1 {
      font-size: 4vh;
      margin-bottom: 1vh;
    }
    height: 20vh;
    width: 80vh;
    font-family: cursive;
    font-size: 3vh;
    color: #fff;
    line-height: 3vh;
  }
}
.loginMsg {
  width: 50vh;
  height: 40vh;
  background-image: url("../assets/login.png");
  background-size: cover;
  background-size: 100% 100%;
  font-size: 2vh;
  font-family: STHupo;
}
.loginB {
  width: 14.4vh;
  height: 6vh;
  background-image: url("../assets/loginB.png");
  background-size: cover;
  background-size: 100% 100%;
  position: absolute;
  bottom: 4%;
  left: 40%;
  &:hover {
    transform: scale(1.04);
    transition: all 0.2s;
  }
  cursor: pointer;
}
</style>

