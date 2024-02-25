import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Image,
  ImageBackground,
  FlatList,
  ScrollView,
} from "react-native";
import { CircularProgress } from "react-native-circular-progress";
import Svg, { Circle } from "react-native-svg";

function MyPage({ route }) {
  const { accessToken, refreshToken } = route.params;
  const [articlesReadThisWeek, setArticlesReadThisWeek] = useState(0);
  const [totalArticlesGoal, setTotalArticlesGoal] = useState(0);
  const [quizScoreThisWeek, setQuizScoreThisWeek] = useState(0);
  const [totalQuizScoreGoal, setTotalQuizScoreGoal] = useState(0);
  const [nickname, setNickName] = useState(null);
  const [tier, setTier] = useState(null);
  const [opacity, setOpacity] = useState(null);

  const fetchMyPage = async () => {
    try {
      const response = await fetch("http://35.216.92.188:8080/api/analytics/", {
        method: "GET",
        headers: {
          ACCESS_TOKEN: `Bearer ${accessToken}`,
          REFRESH_TOKEN: refreshToken,
        },
      });
      if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.status}`);
      }

      const json = await response.json();
      console.log("fetch data", json);
      setArticlesReadThisWeek(7);
      setTotalArticlesGoal(json.weekly_read_goal);
      setQuizScoreThisWeek(26);
      setTotalQuizScoreGoal(json.weekly_quiz_goal);
      setNickName(json.nickname);
      setTier(json.tier);
    } catch (error) {
      console.error("Error fetching news:", error);
    }
  };

  useEffect(() => {
    fetchMyPage();
  }, []);

  const rate = articlesReadThisWeek / totalArticlesGoal;

  const badges = [
    { id: "1", src: require("./assets/img/badges/badge1.png") },
    { id: "2", src: require("./assets/img/badges/badge2.png") },
    { id: "3", src: require("./assets/img/badges/badge3.png") },
    { id: "4", src: require("./assets/img/badges/badge4.png") },
    { id: "5", src: require("./assets/img/badges/badge1.png") },
    { id: "6", src: require("./assets/img/badges/badge2.png") },
    { id: "7", src: require("./assets/img/badges/badge3.png") },
    { id: "8", src: require("./assets/img/badges/badge4.png") },
  ];

  const onPress = () => {};

  useEffect(() => {
    if (rate < 0.3) {
      setOpacity(0.6);
    } else if (rate < 0.5) {
      setOpacity(0.65);
    } else if (rate < 0.7) {
      setOpacity(0.7);
    } else {
      setOpacity(rate);
    }
  }, []);

  return (
    <View style={styles.container}>
      <View style={styles.logoContainer}>
        <Image
          style={styles.image}
          source={require("./assets/img/truetree_logo.png")}
        />
        <Text style={styles.logoText}>TRUETREE</Text>
      </View>
      <View style={styles.seperatorOne} />
      <ScrollView style={styles.scroll} showsVerticalScrollIndicator={false}>
        <View style={styles.nameContainer}>
          <Text style={styles.name}>Hi, {nickname}</Text>
          <Text style={styles.name}>You are 20th place</Text>
          <Text style={styles.name}>in {tier} tier</Text>
        </View>

        <View style={styles.progressContainer}>
          <View style={styles.progressContainerStem}>
            <Svg height="170" width="170" viewBox="0 0 100 100">
              <Circle
                cx="50"
                cy="50"
                r="45"
                fill="#571b01"
                fillOpacity={opacity}
              />
            </Svg>
            <ImageBackground
              source={require("./assets/img/myPageTree.png")}
              style={styles.imageBackground}
            ></ImageBackground>
          </View>
          <View style={styles.progressContainerCircle}>
            <CircularProgress
              size={200}
              width={13}
              fill={(quizScoreThisWeek / totalQuizScoreGoal) * 100}
              tintColor="#5B882C"
              backgroundColor="#cccccc"
              rotation={0}
              lineCap="round"
            ></CircularProgress>
          </View>
        </View>
        <View style={styles.progressTextContainer}>
          <Text style={styles.progressTextTitle}>Progress of the Week</Text>
          <Text style={styles.progressText}>
            read articles : {articlesReadThisWeek}/{totalArticlesGoal}
          </Text>
          <Text style={styles.progressText}>
            total daily quiz scores: {quizScoreThisWeek}/{totalQuizScoreGoal}
          </Text>
          <Text style={styles.bonusScoreText}>Bonus Score : 10 points</Text>
          <Text style={styles.bonusScoreExplain}>
            (You can get it if you achive your goals)
          </Text>
          <Text style={styles.badgeText}>🎖️ Your Badges 🎖️</Text>
        </View>
        <FlatList
          style={styles.badgeList}
          data={badges}
          renderItem={({ item }) => (
            <Image source={item.src} style={styles.badge} />
          )}
          keyExtractor={(item) => item.id}
          horizontal={true}
          showsHorizontalScrollIndicator={false}
        />
        <TouchableOpacity style={styles.button} onPress={onPress}>
          <Text style={styles.text}>Edit Profile</Text>
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    alignItems: "center",
  },
  logoContainer: {
    flexDirection: "row",
    alignItems: "center",
    marginHorizontal: "3%",
    maxHeight: "10%",
    paddingTop: "1%",
  },
  image: {
    width: 40,
    height: 40,
    marginLeft: "3%",
  },
  logoText: {
    fontSize: 20,
    color: "#5B882C",
    fontWeight: "400",
    flex: 1,
    textAlign: "center",
    right: "260%",
  },
  seperatorOne: {
    marginTop: 5,
    marginBottom: 8,
    height: 3,
    width: "100%",
    backgroundColor: "#5B882C",
    borderWidth: 1,
    borderColor: "#5B882C",
  },
  seperatorTwo: {
    height: 1,
    width: "100%",
    backgroundColor: "#5B882C",
    borderWidth: 1,
    borderColor: "#5B882C",
    marginTop: 5,
    marginBottom: 8,
  },
  nameContainer: {
    alignSelf: "stretch",
    padding: 20,
    paddingTop: 10,
  },
  name: {
    fontSize: 18,
    color: "#55433B",
    fontWeight: "500",
  },
  imageBackground: {
    width: 170,
    height: 170,
    position: "absolute",
    top: "-1%",
    left: "0%",
  },
  progressContainer: {
    flexDirection: "column",
    justifyContent: "center",
    alignItems: "center",
    maxHeight: 500,
    backgroundColor: "tomato",
    marginTop: "22%",
  },
  progressContainerStem: {
    alignItems: "center",
    justifyContent: "center",
    marginVertical: 12,
    position: "absolute",
  },
  progressContainerCircle: {
    alignItems: "center",
    justifyContent: "center",
    marginVertical: 12,
    position: "absolute",
  },
  progressTextContainer: {
    marginTop: "35%",
    maxHeight: "20%",
  },
  progressTextTitle: {
    textAlign: "center",
    fontSize: 20,
    fontWeight: "bold",
    top: "-30%",
    color: "#55433B",
    marginVertical: 10,
    marginBottom: 3,
  },
  progressText: {
    textAlign: "center",
    fontSize: 15,
    fontWeight: "bold",
    top: "-30%",
    color: "#55433B",
  },
  bonusScoreText: {
    textAlign: "center",
    fontSize: 15,
    fontWeight: "bold",
    top: "-30%",
    color: "#5B882C",
  },
  bonusScoreExplain: {
    textAlign: "center",
    fontSize: 13,
    fontWeight: "bold",
    top: "-30%",
    color: "#5B882C",
  },
  badgeText: {
    textAlign: "center",
    fontSize: 20,
    fontWeight: "bold",
    color: "#55433B",
    top: "-20%",
  },
  badgeList: {
    marginHorizontal: 13,
    marginTop: 25,
  },
  badge: {
    width: 60,
    height: 60,
    marginHorizontal: 5,
    marginVertical: 5,
  },
  button: {
    backgroundColor: "#5B882C",
    padding: 10,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    maxWidth: "50%",
    marginHorizontal: "25%",
    marginVertical: "5%",
  },
  text: {
    color: "white",
    fontSize: 16,
  },
  scroll: {
    minWidth: "100%",
  },
});

export default MyPage;
