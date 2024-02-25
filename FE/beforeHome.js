import { ImageBackground, View, Text, StyleSheet } from "react-native";
import React, { useState, useRef, useEffect } from "react";

const BeforeHome = () => {
  useEffect(() => {}, [styles]);

  return (
    <ImageBackground
      source={require("./assets/img/truetree_background.png")}
      style={styles.backgroundImage}
    ></ImageBackground>
  );
};

const styles = StyleSheet.create({
  backgroundImage: {
    flex: 1,
    width: "100%",
    height: "100%",
    position: "relative",
    justifyContent: "center",
    alignItems: "center",
  },
  text: {
    textAlign: "center",
    color: "#f25900",
    fontSize: 40,
    fontWeight: "400",
    bottom: "10%",
  },
});

export default BeforeHome;
