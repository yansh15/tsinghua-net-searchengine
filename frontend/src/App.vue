<template>
  <div id="app">
    <!--div id="nav">
      <router-link to="/">Home</router-link> |
      <router-link to="/result">Result</router-link>
    </div-->
    <router-view/>
  </div>
</template>

<style lang="scss">
body {
  margin: 0;
  padding: 0;
}

#app {
  position: absolute;
  height: 100%;
  width: 100%;
  margin: 0;
  padding: 0;
  display: -webkit-flex;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}
</style>
