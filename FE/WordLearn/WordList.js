import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  Button,
  TouchableOpacity,
  FlatList,
  StyleSheet,
  Image,
} from "react-native";
import Swiper from "react-native-swiper";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { QuizHomeScreen, QuizScreen, ResultsScreen } from "./WordQuiz";
import VocabularyLearningScreen from "./WordFlipCard";
import { useNavigation } from "@react-navigation/native";

const QuizStack = createNativeStackNavigator();

const QuizNavigator = ({
  setShowSwiperButtons,
  accessToken,
  refreshToken,
  currentSwipeIndex,
}) => {
  return (
    <QuizStack.Navigator initialRouteName="QuizHome">
      <QuizStack.Screen name="QuizHome" options={{ headerShown: false }}>
        {(props) => (
          <QuizHomeScreen
            {...props}
            setShowSwiperButtons={setShowSwiperButtons}
            accessToken={accessToken}
            refreshToken={refreshToken}
            currentSwipeIndex={currentSwipeIndex}
          />
        )}
      </QuizStack.Screen>
      <QuizStack.Screen name="Quiz" options={{ headerShown: false }}>
        {(props) => (
          <QuizScreen
            {...props}
            setShowSwiperButtons={setShowSwiperButtons}
            accessToken={accessToken}
            refreshToken={refreshToken}
            currentSwipeIndex={currentSwipeIndex}
          />
        )}
      </QuizStack.Screen>
      <QuizStack.Screen name="Results" options={{ headerShown: false }}>
        {(props) => (
          <ResultsScreen
            {...props}
            setShowSwiperButtons={setShowSwiperButtons}
            accessToken={accessToken}
            refreshToken={refreshToken}
            currentSwipeIndex={currentSwipeIndex}
          />
        )}
      </QuizStack.Screen>
    </QuizStack.Navigator>
  );
};

const word_dummy = [
  {
    id: 1,
    word: "단어",
    mean: "뜻",
    createdDate: "2024-02-10T20:26:04.000+00:00",
    modifiedDate: "2024-02-10T20:26:04.000+00:00",
    knowStatus: true,
  },
  {
    id: 2,
    word: "단어2",
    mean: "뜻2",
    createdDate: "2024-02-10T20:26:04.000+00:00",
    modifiedDate: "2024-02-10T20:26:04.000+00:00",
    knowStatus: true,
  },
  {
    id: 3,
    word: "단어3",
    mean: "뜻3",
    createdDate: "2024-02-10T20:26:04.000+00:00",
    modifiedDate: "2024-02-10T20:26:04.000+00:00",
    knowStatus: true,
  },
];

const WordList = ({ route }) => {
  const { accessToken, refreshToken } = route.params;

  const [words, setWords] = useState([]);

  const navigation = useNavigation();

  const fetchWords = async () => {
    try {
      const response = await fetch("http://35.216.92.188:8080/api/words", {
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
      setWords(json);
    } catch (error) {
      console.error("Error fetching words", error);
    }
  };

  useEffect(() => {
    fetchWords();
  }, [currentSwipeIndex]);

  const updateWordStatus = async (wordId, currentStatus) => {
    try {
      const response = await fetch(
        `http://35.216.92.188:8080/api/wordlist/${wordId}`,
        {
          method: "PATCH",
          headers: {
            "Content-Type": "application/json",
            ACCESS_TOKEN: `Bearer ${accessToken}`,
            REFRESH_TOKEN: refreshToken,
          },
          body: JSON.stringify({
            wordId: wordId,
            knowStatus: currentStatus,
          }),
        }
      );

      console.log(response);

      const json = await response.json();

      if (response.ok) {
        console.log(json);
      } else {
        console.error("Error patching");
      }
    } catch (error) {
      console.error(error);
    }
  };

  const handleToggleWordStatus = (wordId, currentStatus) => {
    updateWordStatus(wordId, currentStatus)
      .then(() => {
        setWords(
          words.map((word) => {
            if (word.id === wordId) {
              return { ...word, knowStatus: !currentStatus };
            }
            return word;
          })
        );
      })
      .catch((error) => {
        console.error("Error updating word status:", error);
      });
  };

  const toggleMeanVisibility = (wordId) => {
    setWords(
      words.map((word) => {
        console.log("words.showMean : ", word.showMean);

        if (word.id === wordId) {
          return { ...word, showMean: !word.showMean };
        }
        return word;
      })
    );
  };

  useEffect(() => {}, [words]);

  const renderItem = ({ item }) => (
    <View style={styles.wordContainer}>
      <View style={styles.rowContainer}>
        <TouchableOpacity onPress={() => toggleMeanVisibility(item.id)}>
          <Text style={styles.word}>{item.word}</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={() => handleToggleWordStatus(item.id, item.knowStatus)}
        >
          <Text style={item.knowStatus ? styles.checkMark : styles.crossMark}>
            {item.knowStatus ? "✔" : "❌"}
          </Text>
        </TouchableOpacity>
      </View>
      {item.showMean && (
        <View style={styles.meanContainer}>
          <Text style={styles.mean}>{item.mean}</Text>
        </View>
      )}
    </View>
  );

  const [currentSwipeIndex, setCurrentSwipeIndex] = useState(null);
  const [showSwiperButtons, setShowSwiperButtons] = useState(true);

  return (
    <>
      <View style={[styles.container, { backgroundColor: "white" }]}>
        <View style={styles.logoContainer}>
          <Image
            style={styles.image}
            source={require("../assets/img/truetree_logo.png")}
          />
          <Text style={styles.logoText}>TRUETREE</Text>
        </View>
        <View style={styles.seperatorOne} />
        <Swiper
          loop={false}
          showsPagination={showSwiperButtons}
          scrollEnabled={showSwiperButtons}
          activeDotColor="#FF4C00"
          index={1}
          onIndexChanged={(index) => {
            console.log("Current index:", index);
            setCurrentSwipeIndex(index);
          }}
        >
          <View style={styles.slide}>
            <Text style={styles.resultExplain}>You can check the meaning </Text>
            <Text style={styles.resultExplain}>by clicking on the word</Text>
            <Text style={styles.resultExplain}>
              You can also change the know/unknow status{" "}
            </Text>
            <Text style={styles.resultExplain}>
              by clicking on the toggle icon
            </Text>
            <TouchableOpacity style={styles.button} onPress={fetchWords}>
              <Text style={styles.buttonText}>Reload words</Text>
            </TouchableOpacity>
            {words && words.length > 0 ? (
              <FlatList
                data={words}
                renderItem={renderItem}
                keyExtractor={(item) => item.id.toString()}
              />
            ) : (
              <>
                <Text
                  style={{
                    color: "#FF4C00",
                    textAlign: "center",
                    marginTop: "50%",
                    fontSize: 15,
                  }}
                >
                  Check & Save Words from the article!
                </Text>
              </>
            )}
          </View>
          <QuizNavigator
            setShowSwiperButtons={setShowSwiperButtons}
            accessToken={accessToken}
            refreshToken={refreshToken}
            currentSwipeIndex={currentSwipeIndex}
          />
          <VocabularyLearningScreen
            accessToken={accessToken}
            refreshToken={refreshToken}
            currentSwipeIndex={currentSwipeIndex}
          />
        </Swiper>
      </View>
    </>
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
  wordContainer: {
    paddingVertical: 10,
    paddingHorizontal: 20,
  },
  rowContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  word: {
    fontSize: 18,
    minWidth: "90%",
    color: "#55433B",
  },
  meanContainer: {
    backgroundColor: "#f0f0f0",
    padding: 10,
    borderRadius: 5,
    marginTop: 10,
  },
  mean: {
    fontSize: 16,
    fontStyle: "italic",
    color: "#594e48",
  },
  checkMark: {
    color: "green",
    fontSize: 20,
  },
  crossMark: {
    color: "red",
    fontSize: 20,
  },
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  wordBox: {
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 10,
    alignItems: "center",
  },
  resultExplain: {
    textAlign: "center",
    fontSize: 15,
    marginBottom: 3,
    color: "#55433B",
  },
  button: {
    backgroundColor: "#EB5929",
    padding: 10,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    maxWidth: "50%",
    marginHorizontal: "25%",
    marginVertical: "5%",
  },
  buttonText: {
    color: "white",
    fontSize: 16,
  },
});

export default WordList;
