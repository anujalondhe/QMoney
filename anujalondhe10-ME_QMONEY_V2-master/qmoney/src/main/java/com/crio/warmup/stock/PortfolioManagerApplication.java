package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    List<String> emptyList = new ArrayList<>();
    ObjectMapper objectMapper = getObjectMapper();
    PortfolioTrade[] trades = objectMapper.readValue(f, PortfolioTrade[].class);
    // File f = resolveFileFromResources(args[0]);
    // List<String> emptyList = Collections.emptyList();
    for (PortfolioTrade trade : trades) {
      // System.out.println(trade);
      emptyList.add(trade.getSymbol());
    }
    return emptyList;
    // return Collections.emptyList();

  }


  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    List<PortfolioTrade> res = readTradesFromJson(args[0]);
    RestTemplate restTemplate = new RestTemplate();
    List<TotalReturnsDto> make = new ArrayList<>();
    List<String> fin = new ArrayList<>();
    for (PortfolioTrade p : res) {
      String url = prepareUrl(p, LocalDate.parse(args[1]), getToken());
      TiingoCandle[] obj = restTemplate.getForObject(url, TiingoCandle[].class);
      make.add(new TotalReturnsDto(p.getSymbol(), obj[obj.length - 1].getClose()));
    }
    Collections.sort(make, new Comparator<TotalReturnsDto>() {
      @Override
      public int compare(TotalReturnsDto obj1, TotalReturnsDto obj2) {
        return (int) (obj1.getClosingPrice() - obj2.getClosingPrice());
      }
    });
    for (TotalReturnsDto td : make) {
      fin.add(td.getSymbol());
    }
    return fin;
  }

  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  static String getToken() {
    return "541a08d7e7b6ab4b289749e9e1574a31d5a42c31";
  }

  public static List<PortfolioTrade> readTradesFromJson(String filename)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> sym = new ArrayList<>();
    File file = resolveFileFromResources(filename);
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] obj = om.readValue(file, PortfolioTrade[].class);
    for (PortfolioTrade i : obj) {
      sym.add(i);
    }
    return sym;
  }

  // TODO:
  // Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    String Url = "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate="
        + trade.getPurchaseDate().toString() + "&endDate=" + endDate + "&token=" + token;
    return Url;
    // return new
    // RestTemplate().getForObject("https://api.tiingo.com/tiingo/daily/"+trade+"/prices?startDate=2020-5-10&endDate="+endDate+"&token="token);
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader().getResource(filename).toURI())
        .toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  public static List<String> debugOutputs() {
    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/anujalondhe10-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@34a3d150";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplication.mainReadFile(String[]";
    String lineNumberFromTestFileInStackTrace = "49:1";


    return Arrays.asList(
        new String[] {valueOfArgument0, resultOfResolveFilePathArgs0, toStringOfObjectMapper,
            functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace});
  }



  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.















  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    Candle stockStartDate = candles.get(0);
    double openPrice = stockStartDate.getOpen();
    return openPrice;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    Candle stockLatest = candles.get(candles.size() - 1);
    double closePrice = stockLatest.getClose();
    return closePrice;
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    List<Candle> candlesList = new ArrayList<Candle>();
    String URL = prepareUrl(trade, endDate, token);
    TiingoCandle[] tiingocandles = new RestTemplate().getForObject(URL, TiingoCandle[].class);
    for (TiingoCandle tg : tiingocandles) {
      candlesList.add(tg);
    }
    System.out.println(candlesList);
    return candlesList;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
     List<AnnualizedReturn> annualizedReturns = new ArrayList<>();
       LocalDate endLocalDate = LocalDate.parse(args[1]);

       File trades = resolveFileFromResources(args[0]);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade[] tradesJsons = objectMapper.readValue(trades , PortfolioTrade[].class);
       for(int i=0 ; i < tradesJsons.length; i++){
          annualizedReturns.add(getAnnualizedReturn(tradesJsons[i] , endLocalDate));
       }
     Comparator<AnnualizedReturn> SortByAnnReturn = Comparator.comparing(AnnualizedReturn :: getAnnualizedReturn).reversed();
      Collections.sort(annualizedReturns , SortByAnnReturn);
      return annualizedReturns;
  }
  

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade,
      LocalDate endLocalDate) {
    String ticker = trade.getSymbol();
    LocalDate startLocalDate = trade.getPurchaseDate();

    if (startLocalDate.compareTo(endLocalDate) >= 0) {
      throw new RuntimeException();
    }
    String url = String.format(
        "https://api.tiingo.com/tiingo/daily/%s/prices?" + "startDate=%s&endDate=%s&token=%s",
        ticker, startLocalDate.toString(), endLocalDate.toString(), TOKEN);
    RestTemplate restTemplate = new RestTemplate();
    TiingoCandle[] stocksStartToEndDate = restTemplate.getForObject(url, TiingoCandle[].class);
    if (stocksStartToEndDate != null) {
      TiingoCandle stockStarDate = stocksStartToEndDate[0];
      TiingoCandle stockLatest = stocksStartToEndDate[stocksStartToEndDate.length - 1];

      Double buyPrice = stockStarDate.getOpen();
      Double sellPrice = stockLatest.getClose();

      AnnualizedReturn annualizedReturn =
          calculateAnnualizedReturns(endLocalDate, trade, buyPrice, sellPrice);
      return annualizedReturn;
    } else {
      return new AnnualizedReturn(ticker, Double.NaN, Double.NaN);
    }
  }


  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      // calculate total return
    Double absReturn = (sellPrice - buyPrice) / buyPrice;
    String symbol = trade.getSymbol();
    LocalDate purchasedate = trade.getPurchaseDate();
    // calculate years
    Double numYears = (double) ChronoUnit.DAYS.between(purchasedate, endDate) / 365;
    // calculate annualzied returns using formula
    Double annualized_returns = Math.pow((1 + absReturn), (1 / numYears)) - 1;

    return new AnnualizedReturn(symbol, annualized_returns, absReturn);
  }

  public static final String TOKEN = "541a08d7e7b6ab4b289749e9e1574a31d5a42c31";


  














  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String filename = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade[] portfolioTrades = objectMapper.readValue(filename, PortfolioTrade[].class);
       RestTemplate restTemplate = new RestTemplate();
       PortfolioManager portfolioManager =
           PortfolioManagerFactory.getPortfolioManager(restTemplate);
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
       
  }
  
  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager =
      PortfolioManagerFactory.getPortfolioManager(restTemplate);

  private static String readFileAsString(String file) throws URISyntaxException, IOException {
    return new String(Files.readAllBytes(resolveFileFromResources(file).toPath()), "UTF-8");
  }
  
  



  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());




   // printJsonObject(mainCalculateReturnsAfterRefactor(args));
  }
}

