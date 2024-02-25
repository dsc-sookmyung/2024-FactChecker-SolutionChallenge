import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  Switch,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  TextInput,
  ActivityIndicator,
} from "react-native";
import { useRoute } from "@react-navigation/native";
import Icon from "react-native-vector-icons/FontAwesome5";
import { API_KEY } from "@env";

const LearnScreen = ({ route }) => {
  const { accessToken, refreshToken, articleId, prevPage } = route.params;

  console.log(articleId);

  const [title, setTitle] = useState(null);
  const [article, setArticle] = useState(null);

  const fetchNews = async () => {
    if (prevPage === "fact") {
      try {
        const response = await fetch(
          `http://35.216.92.188:8080/api/YoutubeNews/getarticle/${articleId}`,
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

        setTitle(json.title);
        setArticle(json.article);
      } catch (error) {
        console.error("Error fetching news:", error);
      }
    } else {
      try {
        const response = await fetch(
          `http://35.216.92.188:8080/api/interests/getarticle/${articleId}`,
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

        setTitle(json.title);
        setArticle(json.article);
      } catch (error) {
        console.error("Error fetching news:", error);
      }
    }
  };

  useEffect(() => {
    fetchNews();
  }, []);

  const [isSummaryMode, setIsSummaryMode] = useState(false);
  const [highlightMode, setHighlightMode] = useState(false);
  const [selectedWords, setSelectedWords] = useState([]);
  const [response, setResponse] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [showBox, setShowBox] = useState(false);
  const [dictionaryList, setDictionaryList] = useState([]);
  const [word, setWord] = useState("");
  const [mean, setMean] = useState("");

  const addDictionary = (word, mean, selectedWordsGroup) => {
    if (dictionaryList.some((item) => item.word === word)) {
      console.log("이미 추가된 단어입니다.");
      return;
    }

    const newDictionary = { word, mean };
    setDictionaryList([...dictionaryList, newDictionary]);

    setWord("");
    setMean("");
  };

  const deleteDictionary = (wordToDelete) => {
    const indexToDelete = dictionaryList.findIndex(
      (item) => item.word === wordToDelete
    );

    if (indexToDelete !== -1) {
      const newList = dictionaryList.filter(
        (_, index) => index !== indexToDelete
      );
      setDictionaryList(newList);
    } else {
      console.log("삭제할 단어를 찾을 수 없습니다.");
    }
  };

  const words = article && article.split(/\s+/).filter((word) => word !== "");
  const { GoogleGenerativeAI } = require("@google/generative-ai");

  const genAI = new GoogleGenerativeAI(process.env.API_KEY);
  const model = genAI.getGenerativeModel({ model: "gemini-pro" });

  async function fetchResponse(word, selectedWordsGroup) {
    setIsLoading(true);
    const safety_settings = [
      {
        category: "HARM_CATEGORY_HARASSMENT",
        threshold: "BLOCK_NONE",
      },
      {
        category: "HARM_CATEGORY_HATE_SPEECH",
        threshold: "BLOCK_NONE",
      },
      {
        category: "HARM_CATEGORY_SEXUALLY_EXPLICIT",
        threshold: "BLOCK_NONE",
      },
      {
        category: "HARM_CATEGORY_DANGEROUS_CONTENT",
        threshold: "BLOCK_NONE",
      },
    ];

    const model = genAI.getGenerativeModel({
      model: "gemini-pro",
      safety_settings,
    });
    const prompt = `${selectedWordsGroup} -> 이 맥락에서 ${word}가 쓰였어. 이 때 이 맥락에서 ${word}의 뜻을 사전에서 찾아 1~2줄 이내로 아주 이해하기 쉽게 알려줘. 답변의 총 길이는 1~2줄 이내여야 해. 그리고 답변의 스타일은 사전에 쓰인 뜻을 그대로 알려주는 느낌으로 답변해줘.`;
    const result = await model.generateContent(prompt);
    const response = await result.response;
    const text = response.text();
    setIsLoading(false);
    addDictionary(word, text, selectedWordsGroup);
    return text;
  }

  const selectWord = async (word, selectedWordsGroup) => {
    if (highlightMode) {
      if (selectedWords.includes(word)) {
        setSelectedWords(selectedWords.filter((w) => w !== word));
        setShowBox(false);
        deleteDictionary(word);
      } else {
        setSelectedWords([...selectedWords, word]);

        try {
          const text = await fetchResponse(word, selectedWordsGroup);
          setResponse(`${word} means... \n ${text}`);
          setShowBox(true);
        } catch {
          setResponse(
            "Server response Error. Try highlighting the word again!"
          );
          setShowBox(true);
          setIsLoading(false);
        }
      }
    }
  };

  useEffect(() => {
    if (showBox & !isLoading) {
      setTimeout(() => {
        setShowBox(false);
      }, 4000);
    }
  }, [showBox, isLoading]);

  const toggleHighlightMode = () => {
    if (highlightMode) {
      sendWordsToBackend();
      setSelectedWords([]);
    }
    setHighlightMode(!highlightMode);
  };

  const sendWords = async () => {
    try {
      const response = await fetch(
        "http://35.216.92.188:8080/api/learn/words",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            ACCESS_TOKEN: `Bearer ${accessToken}`,
            REFRESH_TOKEN: refreshToken,
          },
          body: JSON.stringify({
            words: dictionaryList,
          }),
        }
      );

      console.log(response);

      const json = await response.json();

      if (response.ok) {
        console.log(json);
      } else {
        console.error("ResultsScreen : Error fetching words");
      }
    } catch (error) {
      console.error(error);
    }
  };

  const sendWordsToBackend = () => {
    setDictionaryList([]);
    sendWords();
  };

  const dummy_feedback =
    "피드백이다.......ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ";

  const [inputText, setInputText] = useState();
  const [feedback, setFeedback] = useState("get a feedback here!");
  const [isFeedbackLoading, setIsFeedbackLoading] = useState(false);

  async function fetchFeedback(summary) {
    setIsFeedbackLoading(true);

    try {
      const safety_settings = [
        {
          category: "HARM_CATEGORY_HARASSMENT",
          threshold: "BLOCK_NONE",
        },
        {
          category: "HARM_CATEGORY_HATE_SPEECH",
          threshold: "BLOCK_NONE",
        },
        {
          category: "HARM_CATEGORY_SEXUALLY_EXPLICIT",
          threshold: "BLOCK_NONE",
        },
        {
          category: "HARM_CATEGORY_DANGEROUS_CONTENT",
          threshold: "BLOCK_NONE",
        },
      ];
      const model = genAI.getGenerativeModel({
        model: "gemini-pro",
        safety_settings,
      });
      const prompt = `${article} + "이 본문을 가지고 요약을 1~3줄로 다음과 같이 해봤어 : " + ${summary} + '이 기사 내용을 잘 요약한건지 3~4줄로 피드백을 해주고 더 나은 요약을 제공해줘. 답변 길이는 3~4줄 이내여야 해.`;
      const result = await model.generateContent(prompt);
      const response = await result.response;
      const text = response.text();
      return text;
    } catch {
      return "서버 응답 오류";
    }
  }

  onPress = async () => {
    const feedback = await fetchFeedback(inputText);
    setFeedback(feedback);
    setIsFeedbackLoading(false);
  };

  return (
    <View style={[styles.container, { backgroundColor: "white" }]}>
      <View style={styles.toggleContainer}>
        <Switch
          onValueChange={() => setIsSummaryMode(!isSummaryMode)}
          value={isSummaryMode}
          style={styles.toggle}
          trackColor={{ false: "#c9c9c9", true: "#c9c9c9" }}
          thumbColor={isSummaryMode ? "#EB5929" : "#EB5929"}
        />
        <Text style={styles.modeText}>
          {isSummaryMode ? "Summarize Mode" : "Check Words Mode"}
        </Text>
        {isSummaryMode ? (
          <Text style={styles.nonHighlightModeButton}></Text>
        ) : (
          <TouchableOpacity
            style={styles.highlightModeButton}
            onPress={toggleHighlightMode}
          >
            <Text style={{ textAlign: "center" }}>
              {highlightMode ? (
                `Save\nwords`
              ) : (
                <Icon name="highlighter" size={30} color="#ff0" />
              )}
            </Text>
          </TouchableOpacity>
        )}
      </View>
      {isSummaryMode ? (
        <View style={styles.explainTexts}>
          <Text style={styles.explainText}>
            Summarize and get a brief feedback!
          </Text>
        </View>
      ) : (
        <View style={styles.explainTexts}>
          <Text style={styles.explainText}>
            check the words you don't know!
          </Text>
          <Text style={styles.explainText}>
            you can save the word & meaning in your vocab list
          </Text>
        </View>
      )}
      <ScrollView
        showsVerticalScrollIndicator={false}
        style={isSummaryMode ? styles.summaryArticle : styles.article}
      >
        {isSummaryMode ? (
          <>
            <TextInput
              style={styles.textInput}
              multiline={true}
              onChangeText={(text) => setInputText(text)}
              value={inputText}
              placeholder="Summarize here!"
            ></TextInput>
            <TouchableOpacity style={styles.button} onPress={onPress}>
              <Text style={styles.buttonText}>Get a Feedback!</Text>
            </TouchableOpacity>
            <View style={styles.feedbackBox}>
              {isFeedbackLoading ? (
                <>
                  <ActivityIndicator size="large" color="#EB5929" />
                  <Text style={{ textAlign: "center", marginTop: 10 }}>
                    Recieving Feedback...
                  </Text>
                </>
              ) : (
                <Text style={styles.feedbackText}>{feedback}</Text>
              )}
            </View>
          </>
        ) : (
          <View style={styles.articleText}>
            {highlightMode
              ? words &&
                words.map((word, index) => (
                  <TouchableOpacity
                    key={index}
                    onPress={() => {
                      const selectedWordsGroup = [
                        ...(index > 1 ? [words.slice(index - 2, index)] : []),
                        word,
                        ...(index < words.length - 2
                          ? [words.slice(index, index + 2)]
                          : []),
                      ].join(" ");
                      console.log(selectedWordsGroup);
                      selectWord(word, selectedWordsGroup);
                    }}
                    style={
                      highlightMode && selectedWords.includes(word)
                        ? styles.highlightedWord
                        : styles.word
                    }
                  >
                    <Text style={styles.wordSize}>
                      {word}
                      {index < words.length - 1 ? " " : ""}
                    </Text>
                  </TouchableOpacity>
                ))
              : words &&
                words.map((word, index) => (
                  <View
                    key={index}
                    style={
                      highlightMode && selectedWords.includes(word)
                        ? styles.highlightedWord
                        : styles.word
                    }
                  >
                    <Text style={styles.wordSize}>
                      {word}
                      {index < words.length - 1 ? " " : ""}
                    </Text>
                  </View>
                ))}
          </View>
        )}
      </ScrollView>
      {isLoading && (
        <View style={styles.overlay}>
          <ActivityIndicator size="large" color="#EB5929" />
        </View>
      )}
      {showBox && (
        <View style={styles.overlay}>
          <Text style={styles.responseText}>{response}</Text>
        </View>
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
  title: {
    alignSelf: "flex-start",
    padding: 20,
    fontSize: 20,
  },
  toggleContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    margin: 20,
    backgroundColor: "#f0f0f0",
    borderRadius: 50,
  },
  toggle: {
    position: "relative",
    left: "-63%",
    transform: [{ scaleX: 1.3 }, { scaleY: 1.3 }],
    trackColor: { false: "#767577", true: "orange" },
    thumbColor: "#f4f3f4",
    ios_backgroundColor: "#3e3e3e",
    paddingTop: 10,
    paddingHorizontal: 20,
  },
  modeText: {
    textAlign: "center",
    left: "-26%",
    fontWeight: "bold",
    color: "#55433B",
  },
  explainTexts: {
    textAlign: "center",
    marginBottom: 10,
    top: -10,
  },
  explainText: {
    textAlign: "center",
    color: "#55433B",
  },
  summaryArticle: {
    fontSize: 16,
    lineHeight: 24,
    paddingHorizontal: 20,
  },
  container: {
    flex: 1,
    margin: 10,
  },
  highlightModeButton: {
    padding: 10,
    backgroundColor: "#ddd",
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderRadius: 100,
  },
  nonHighlightModeButton: {
    width: 55,
    height: 55,
    padding: 10,
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderRadius: 100,
  },
  highlightedWord: {
    backgroundColor: "#FFFC70",
    marginVertical: 5,
  },
  articleText: {
    flexDirection: "row",
    flexWrap: "wrap",
    marginHorizontal: 20,
  },
  word: {
    marginVertical: 5,
  },
  wordSize: {
    fontSize: 18,
  },
  textInput: {
    borderWidth: 1.2,
    borderColor: "#c9c9c9",
    backgroundColor: "#fafafa",
    borderRadius: 10,
    padding: 10,
    height: 150,
    textAlign: "center",
  },
  button: {
    backgroundColor: "#EB5929",
    padding: 10,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    minWidth: "50%",
    marginTop: 10,
  },
  buttonText: {
    color: "white",
    fontSize: 16,
  },
  feedbackBox: {
    marginTop: 20,
  },
  feedbackText: {
    color: "#55433B",
    fontSize: 15,
    textAlign: "center",
  },
  overlay: {
    position: "absolute",
    maxWidth: "100%",
    minHeight: 70,
    left: "5%",
    right: "5%",
    bottom: 20,
    justifyContent: "center",
    alignItems: "center",
    textAlign: "center",
    backgroundColor: "#ffc4b0",
    borderRadius: 15,
    paddingVertical: 2,
    paddingHorizontal: 3,
  },
  responseText: {
    color: "#55433B",
    fontSize: 15,
    textAlign: "center",
  },
});

export default LearnScreen;
