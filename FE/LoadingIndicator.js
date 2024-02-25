import React, { useCallback, useEffect, useRef } from "react";
import { View, StyleSheet, Animated } from "react-native";

const LoadingSpinner = () => {
  const animationValue = useRef(new Animated.Value(0)).current;

  const startAnimation = useCallback(() => {
    Animated.loop(
      Animated.timing(animationValue, {
        toValue: 1,
        duration: 700,
        useNativeDriver: true,
      })
    ).start();
  }, [animationValue]);

  useEffect(() => {
    startAnimation();
  }, [startAnimation]);

  const RotateData = animationValue.interpolate({
    inputRange: [0, 1],
    outputRange: ["0deg", "360deg"],
  });

  return (
    <View style={styles.container}>
      <Animated.Image
        style={{
          transform: [{ rotate: RotateData }],
          width: 30,
          height: 30,
        }}
        source={{
          uri: "https://res.cloudinary.com/df9jsefb9/image/upload/c_scale,h_84,q_auto/v1501869525/assets/idc-loading-t_3x.png",
        }}
      />
    </View>
  );
};

export default LoadingSpinner;

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
    justifyContent: "center",
    maxWidth: 40,
    maxHeight: 40,
  },

  spinnerImage: {
    maxWidth: 20,
    maxHeight: 20,
  },
});
