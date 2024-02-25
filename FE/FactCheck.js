import React, { useState, useEffect } from "react";
import {
  TouchableOpacity,
  Text,
  View,
  StyleSheet,
  FlatList,
  Image,
  TextInput,
  ActivityIndicator,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";
import Svg, { Circle } from "react-native-svg";
import LoadingIndicator from "./LoadingIndicator";

export default function FactCheck({ route }) {
  const { accessToken, refreshToken } = route.params;

  const [videos, setVideos] = useState([
    {
      curr_youtube_news: [],
      id: 1,
      keyword: "will be shown here",
      loadingStatus: false,
      rel_youtube_news: [],
      show: false,
      upload_date: "date",
      url: "",
      yt_title: "enter url and verify youtube videos",
    },
  ]);

  const [url, setUrl] = useState(null);

  const fetchNews = async () => {
    try {
      const response = await fetch(
        "http://35.216.92.188:8080/api/YoutubeNews/getarticles",
        {
          method: "GET",
          headers: {
            ACCESS_TOKEN: `Bearer ${accessToken}`,
            REFRESH_TOKEN: refreshToken,
          },
        }
      );
      if (!response.ok) {
        throw new Error(`Network response was not ok: ${response.status}`);
      }

      const prevLength = videos.length;
      const json = await response.json();
      console.log("fetch data", json);
      setVideos(json.reverse());
    } catch (error) {
      console.error("Error fetching news:", error);
    }
  };

  useEffect(() => {
    fetchNews();
  }, []);

  const sendNews = async () => {
    if (url && url.trim() !== "") {
      console.log("send");
      try {
        const response = await fetch(
          "http://35.216.92.188:8080/api/YoutubeNews/add",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              ACCESS_TOKEN: `Bearer ${accessToken}`,
              REFRESH_TOKEN: refreshToken,
            },
            body: JSON.stringify({
              url: url.trim(),
            }),
          }
        );

        const json = await response.json();

        if (response.ok) {
          console.log("send url response data : ", json);
          fetchNews();
        } else {
          console.error("Error sendung url");
        }
      } catch (error) {
        console.error(error);
      }
    }
  };

  const sendUrl = async () => {
    sendNews();
    setUrl(null);
  };

  const navigation = useNavigation();
  const handleItemClick = (articleId) => {
    const prevPage = "fact";
    navigation.navigate("Check words & Summarize", { articleId, prevPage });
  };

  const renderArticleItem = ({ item }) => (
    <LinearGradient colors={["#FFFFFF", "#FFFFFF"]} style={styles.newsBox}>
      <TouchableOpacity
        activeOpacity={1}
        onPress={() => handleItemClick(item.id)}
        style={styles.articleItem}
      >
        <View style={styles.articleLeft}>
          <Text style={styles.articleTitle}>{item.title}</Text>
          <Text style={styles.dateText}>{item.date.slice(0, 10)}</Text>
        </View>
        <Text style={styles.credibilityText}>
          {parseInt(item.credibility * 100)}% True
        </Text>
      </TouchableOpacity>
    </LinearGradient>
  );

  const toggleNewsVisibility = (youtubeNewsId) => {
    setVideos((prevVideos) =>
      prevVideos.map((news) => {
        if (news.id === youtubeNewsId) {
          return { ...news, show: !news.show };
        }
        return news;
      })
    );
  };

  const renderVideoItem = ({ item }) => (
    <View>
      {item.loadingStatus ? (
        <>
          <TouchableOpacity activeOpacity={1}>
            <LinearGradient
              colors={["#FFDAAD", "#FFDAAD"]}
              style={styles.containerYT}
            >
              <ActivityIndicator size="large" color="#EB5929" />
              <View style={styles.YTContent}>
                <Text style={styles.YTTitle}>
                  No verified youtube articles yet
                </Text>
              </View>
            </LinearGradient>
          </TouchableOpacity>
        </>
      ) : (
        <>
          <TouchableOpacity
            activeOpacity={1}
            onPress={() => toggleNewsVisibility(item.id)}
          >
            <LinearGradient
              colors={["#FFDAAD", "#FFDAAD"]} // Use the two main colors of your gradient
              style={styles.containerYT}
            >
              <Image
                style={styles.imageYT}
                source={require("./assets/img/video.png")} // 이미지 파일 경로 지정
              />
              <View style={styles.YTContent}>
                <Text style={styles.YTTitle}>{item.yt_title}</Text>
                <Text style={styles.YTdate}>
                  {item.upload_date.slice(0, 10)}
                </Text>
              </View>
            </LinearGradient>
          </TouchableOpacity>
        </>
      )}
      {item.loadingStatus ? (
        <Text style={styles.keywordText}>
          Searching & Verifying articles...
        </Text>
      ) : (
        <Text style={styles.keywordText}>
          Searched Keyword : {item.keyword}
        </Text>
      )}
      {item.show && (
        <View style={styles.newsContainer}>
          <Text style={styles.newsText}>🐿️ Most Recent</Text>
          <FlatList
            data={item.curr_youtube_news}
            renderItem={renderArticleItem}
            keyExtractor={(article) => "curr_" + article.id}
          />
          <Text style={styles.newsText}>🐿️ Most Related</Text>
          <FlatList
            data={item.rel_youtube_news}
            renderItem={renderArticleItem}
            keyExtractor={(article) => "rel_" + article.id}
          />
        </View>
      )}
    </View>
  );

  return (
    <>
      <View style={styles.container}>
        <View style={styles.logoContainer}>
          <Image
            style={styles.image}
            source={require("./assets/img/truetree_logo.png")} // 이미지 파일 경로 지정
          />
          <Text style={styles.logoText}>TRUETREE</Text>
        </View>
        <View style={styles.seperatorOne} />
        {videos.length > 0 ? (
          <FlatList
            data={videos}
            renderItem={renderVideoItem}
            keyExtractor={(video, index) => index.toString()}
            showsVerticalScrollIndicator={false}
            style={styles.allNewsContainer}
            ListHeaderComponent={
              <>
                <Text style={styles.textOne}>Is this youtube news true?</Text>
                <Text style={styles.textTwo}>
                  Check the facts through related articles
                </Text>
                <View style={styles.centeredImageContainer}>
                  <Image
                    style={styles.imageIsTrue}
                    source={require("./assets/img/isItTrue.png")}
                  />
                </View>
                <View>
                  <TextInput
                    placeholder="Enter youtube url"
                    value={url}
                    onChangeText={setUrl}
                    style={styles.input}
                  />
                  <TouchableOpacity
                    style={styles.verifyButtonContainer}
                    onPress={sendUrl}
                  >
                    <View style={styles.verifyButton}>
                      <Text style={styles.verifyButtonText}>Verify</Text>
                    </View>
                  </TouchableOpacity>
                  <Text
                    style={{
                      fontSize: 15,
                      textAlign: "center",
                      color: "#55433B",
                    }}
                  >
                    Takes about 3~4 miniutes to verify
                  </Text>
                  <TouchableOpacity
                    style={styles.verifyButtonContainer}
                    onPress={fetchNews}
                  >
                    <View>
                      <Text
                        style={{
                          fontSize: 18,
                          textAlign: "center",
                          color: "#EB5929",
                          marginBottom: 10,
                        }}
                      >
                        Reload verified videos
                      </Text>
                    </View>
                  </TouchableOpacity>
                </View>
              </>
            }
          />
        ) : (
          <>
            {/* <View>
              {loadingStatus ? (
                <ActivityIndicator
                  style={styles.loading}
                  size="large"
                  color="#EB5929"
                />
              ) : null}
            </View> */}
          </>
        )}
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FFFFFF",
    justifyContent: "center",
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
  imageIsTrue: {
    marginTop: 5,
    marginBottom: 20,
    right: "8%",
  },
  textOne: {
    fontSize: 23,
    color: "#55433B",
    fontWeight: "bold",
    textAlign: "center",
  },
  textTwo: {
    fontSize: 18,
    color: "#55433B",
    fontWeight: "500",
    fontStyle: "italic",
    textAlign: "center",
  },
  containerYT: {
    borderColor: "#FF4C00",
    // borderWidth: 1.3,
    borderRadius: 20,
    minHeight: 80,
    padding: 15,
    flexDirection: "row",
    alignItems: "center",
    minWidth: "90%",
    marginVertical: 10,
  },
  centeredImageContainer: {
    alignItems: "center",
  },
  imageYT: {
    width: 40,
    height: 40,
    justifyContent: "center",
  },
  YTContent: {
    flexDirection: "column",
    marginLeft: 10,
    maxWidth: "70%",
    maxHeight: "100%",
  },
  YTTitle: {
    fontSize: 16,
    fontWeight: "bold",
    color: "#55433B",
  },
  YTdate: {
    fontSize: 13,
    color: "#55433B",
  },
  newsBox: {
    maxWidth: "93%",
    marginBottom: 10,
    margin: 7,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderRadius: 20,
    left: "10%",
  },
  articleList: {
    flex: 1,
    left: "10%",
  },
  articleItem: {
    minWidth: "93%",
    minHeight: 80,
    padding: 10,
    borderWidth: 1,
    borderColor: "#fc7012",
    flexDirection: "row",
    alignItems: "center",
    borderRadius: 20,
  },
  articleLeft: {
    flexDirection: "vertical",
    marginLeft: 10,
    maxWidth: "70%",
  },
  articleTitle: {
    fontWeight: "bold",
    textAlign: "left",
    flexShrink: 1,
    color: "#55433B",
  },
  dateText: {
    color: "#55433B",
  },
  credibilityText: {
    textAlign: "right",
    flex: 1,
    color: "#55433B",
  },
  keywordText: {
    fontSize: 13,
    left: 15,
    color: "#FF4C00",
    fontWeight: "bold",
    marginBottom: 5,
    left: "5%",
  },
  newsText: {
    fontSize: 20,
    left: 15,
    color: "#55433B",
    fontWeight: "500",
    left: "5%",
  },
  input: {
    textAlign: "center",
    backgroundColor: "#e6e6e6",
    borderRadius: 10,
    color: "#55433B",
    paddingVertical: 5,
    marginTop: 3,
    maxWidth: "100%",
  },
  verifyButtonContainer: {
    alignItems: "center",
    justifyContent: "center",
  },
  verifyButton: {
    marginVertical: 10,
    backgroundColor: "#ff6a2b",
    width: "100%",
    borderRadius: 10,
  },
  verifyButtonText: {
    color: "white",
    textAlign: "center",
    fontSize: 20,
    paddingVertical: 5,
  },
  allNewsContainer: {
    flex: 1,
  },
  newsContainer: {
    marginBottom: 10,
  },
  loading: {
    alignItems: "center",
    justifyContent: "center",
    minHeight: "85%",
  },
});
