import React, { useState, useEffect } from "react";
import {
  View,
  ScrollView,
  TouchableOpacity,
  Text,
  FlatList,
  StyleSheet,
  Image,
  ImageBackground,
  ActivityIndicator,
} from "react-native";
import { useNavigation } from "@react-navigation/native";
import { LinearGradient } from "expo-linear-gradient";

const HomeScreen = ({ route }) => {
  const { accessToken, refreshToken } = route.params;

  const fetchNews = async () => {
    try {
      const response = await fetch(
        "http://35.216.92.188:8080/api/interests/getarticle",
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

      const json = await response.json();
      setArticles(json);
    } catch (error) {
      console.error("Error fetching news:", error);
    }
  };

  const fetchInterests = async () => {
    try {
      const response = await fetch(
        "http://35.216.92.188:8080/api/interests/selected",
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

      const json = await response.json();

      return json;
    } catch (error) {
      console.error("Error fetching news:", error);
    }
  };

  const sendPostRequest = async (section_number) => {
    try {
      const response = await fetch(
        `http://35.216.92.188:8080/api/interests/getarticleBySection?section=${section_number}`,
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

      const json = await response.json();
      return json;
    } catch (error) {
      console.error("Error fetching user interest news:", error);
    }
  };

  useEffect(() => {
    fetchNews()
      .then(() => {
        return fetchInterests();
      })
      .then((interestJson) => {
        setUserInterest(interestJson);
        return interestJson;
      })
      .then((interestJson) => {
        console.log("interestJson: ", interestJson);

        return new Promise((resolve) => {
          if (interestJson.length > 0) {
            resolve(
              Promise.all(
                interestJson.map((interest) =>
                  sendPostRequest(sectionNum[interest])
                )
              )
            );
          } else {
            resolve([]);
          }
        });
      })
      .then((allResponses) => {
        const allArticles = allResponses.flat();
        console.log("allArticles : ", allArticles[0]);
        setUserInterestArticles([...allArticles]);
      })
      .catch((error) => {
        console.error("요청 중 오류 발생:", error);
      });
  }, []);

  useEffect(() => {
    console.log(
      "userInterestArticles 길이 : ",
      userInterestArticles ? userInterestArticles.length : null
    );
  }, []);

  const sectionNum = {
    POLITICS: 100,
    ECONOMY: 101,
    SOCIETY: 102,
    CULTURE: 103,
    GLOBAL: 104,
    CULTURE: 105,
  };

  const navigation = useNavigation();
  const [selectedSection, setSelectedSection] = useState(null);
  const [userInterest, setUserInterest] = useState([]);
  const [userInterestArticles, setUserInterestArticles] = useState([]);

  const sections = {
    100: "Politics",
    101: "Economy",
    102: "Society",
    103: "Lifestyle & Culture",
    104: "World",
    105: "IT & Science",
  };

  const [articles, setArticles] = useState([]);

  const handleItemClick = (articleId) => {
    const prevPage = "home";
    navigation.navigate("Check words & Summarize", { articleId, prevPage });
  };

  const filterArticlesBySection = (sectionId) => {
    if (selectedSection === sectionId) {
      setSelectedSection(null);
    } else {
      setSelectedSection(sectionId);
      console.log(sectionId);
    }
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
          <Text style={styles.date}>{item.date}</Text>
        </View>
      </TouchableOpacity>
    </LinearGradient>
  );

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
      <ScrollView
        style={styles.sectionBar}
        horizontal={true}
        showsHorizontalScrollIndicator={false}
      >
        {Object.entries(sections).map(([key, value]) => (
          <TouchableOpacity
            activeOpacity={1}
            key={key}
            style={[
              styles.sectionButton,
              selectedSection === key && styles.sectionButtonSelected,
            ]}
            onPress={() => filterArticlesBySection(key)}
          >
            <Text
              style={
                selectedSection === key
                  ? styles.sectionTextSelected
                  : styles.sectionText
              }
            >
              {value}
            </Text>
          </TouchableOpacity>
        ))}
      </ScrollView>
      {articles.length > 0 && userInterestArticles.length > 0 ? (
        <FlatList
          data={
            parseInt(selectedSection) === 99
              ? userInterestArticles
              : articles.filter(
                  (article) =>
                    selectedSection == null ||
                    article.section === parseInt(selectedSection)
                )
          }
          renderItem={renderArticleItem}
          keyExtractor={(item, index) => index}
          style={styles.articleList}
          showsVerticalScrollIndicator={false}
          ListHeaderComponent={
            <LinearGradient colors={["#f7be7c", "#f5a984"]} style={styles.card}>
              <View style={styles.gradientContainer}>
                <Image
                  source={require("./assets/img/truetree_다람쥐.png")}
                  style={styles.imagesq}
                />
                <View style={styles.gradientTextContainer}>
                  <Text style={styles.text}>Check out</Text>
                  <Text style={styles.text}>the up-to-date articles</Text>
                  <Text style={styles.text}>of your interest</Text>
                  <Text style={styles.text}>with verified reliability!</Text>
                </View>
              </View>
            </LinearGradient>
          }
        />
      ) : (
        <ActivityIndicator
          style={styles.loading}
          size="large"
          color="#EB5929"
        />
      )}
    </View>
  );
};

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
  sectionBar: {
    marginHorizontal: 10,
    paddingHorizontal: 10,
    paddingVertical: 8,
    maxHeight: 50,
  },
  sectionButton: {
    paddingVertical: 5,
    paddingHorizontal: 12,
    backgroundColor: "#e0e0e0",
    borderRadius: 12,
    alignItems: "center",
    justifyContent: "center",
    marginHorizontal: 5,
  },
  sectionButtonSelected: {
    backgroundColor: "#EB5929",
  },
  sectionText: {
    fontSize: 14,
    color: "#55433B",
  },
  sectionTextSelected: {
    fontSize: 14,
    color: "white",
  },
  card: {
    margin: 15,
    borderRadius: 20,
    padding: 25,
    alignItems: "center",
    justifyContent: "center",
    shadowColor: "#EAEAEA",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    elevation: 5,
    maxWidth: "100%",
    minHeight: 100,
    backgroundColor: "#FFFFFF",
  },
  imagesq: {
    width: 100,
    height: 100,
    marginRight: 15,
  },
  gradientContainer: {
    flexDirection: "row",
    alignItems: "center",
    width: "100%",
  },
  gradientTextContainer: {
    flexDirection: "column",
    fontWeight: "300",
    maxHeight: 100,
  },
  text: {
    fontSize: 18,
    fontWeight: "500",
    color: "#55433B",
    textAlign: "right",
    flex: 1,
    height: "7%",
  },
  newsBox: {
    maxWidth: "93%",
    marginBottom: 10,
    margin: 7,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderRadius: 20,
  },
  articleList: {
    flex: 1,
  },
  articleItem: {
    minWidth: "94%",
    minHeight: 80,
    padding: 10,
    borderWidth: 1.2,
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
  loading: {
    alignItems: "center",
    justifyContent: "center",
    minHeight: "80%",
  },
});

export default HomeScreen;
