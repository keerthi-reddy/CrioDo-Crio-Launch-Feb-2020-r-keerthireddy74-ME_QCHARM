package com.crio.qcharm.ds;

import com.crio.qcharm.request.PageRequest;
import com.crio.qcharm.request.SearchRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SourceFileVersionArrayListImpl implements SourceFileVersion{

  String fileName;
  List<String> lines;
  public SourceFileVersionArrayListImpl(SourceFileVersionArrayListImpl obj) {
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  //     FileInfo - contains following information
  //         1. fileName
  //         2. List of lines
  // Steps:
  //     You task here is to construct SourceFileVersionArrayListImpl object by
  //     1. Storing the lines received from fileInfo object
  //     2. Storing the fileName received from fileInfo object.
  // Recommendations:
  //     1. Use Java ArrayList to store the lines received from fileInfo

  public SourceFileVersionArrayListImpl(FileInfo fileInfo) {
    fileName = fileInfo.fileName;
    lines = fileInfo.lines;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting before the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 25, line number 26 ... , line number 48, line number49)

  @Override
  public SourceFileVersion apply(List<Edits> edits) {
    List<String> lines = new ArrayList<>();
    this.lines.addAll(lines);

   // SourceFileVersionArrayListImpl latest = new SourceFileVersionArrayListImpl();
    for (Edits oneEdit : edits) {
      if (oneEdit instanceof UpdateLines) {
        apply((UpdateLines) oneEdit);
      } else {
        assert(oneEdit instanceof SearchReplace);
        apply((SearchReplace) oneEdit);
      }
    }
    return this;
  }


  @Override
  public void apply(SearchReplace searchReplace) {
  }


  // TODO: CRIO_TASK_MODULE_CUT_COPY_PASTE
  // Input:
  //     UpdateLines
  //        1. startingLineNo - starting line number of last time it received page from backend
  //        2. numberOfLines - number of lines received from backend last time.
  //        3. lines - present view of lines in range(startingLineNo,startingLineNo+numberOfLines)
  //        4. cursor
  // Description:
  //        1. Remove the line numbers in the range(starting line no, ending line no)
  //        2. Inserting the lines in new content starting position starting line no
  // Example:
  //        UpdateLines looks like this
  //            1. start line no - 50
  //            2. numberOfLines - 10
  //            3. lines - ["Hello world"]
  //
  //       Assume the file has 100 lines in it
  //
  //       File contents before edit:
  //       ==========================
  //       line no 0
  //       line no 1
  //       line no 2
  //          .....
  //       line no 99
  //
  //        File contents After Edit:
  //        =========================
  //        line no 0
  //        line no 1
  //        line no 2
  //        line no 3
  //         .....
  //        line no 49
  //        Hello World
  //        line no 60
  //        line no 61
  //          ....
  //        line no 99
  //



  @Override
  public void apply(UpdateLines updateLines) {
    List<String> content = updateLines.getLines();
    int startingLineNo = updateLines.getStartingLineNo();
    int numberOfLines = updateLines.getNumberOfLines();
    List<String> dupLines = new ArrayList<String>();
    for(int i = 0;i <= startingLineNo;i++) {
      dupLines.add(lines.get(i));
    }
    for(int i = 0;i < content.size();i++) {
      dupLines.add(content.get(i));
    }
    for(int i = startingLineNo+numberOfLines;i < lines.size();i++) {
      dupLines.add(lines.get(i));
    }
    lines = dupLines;
  }

  @Override
  public Page getLinesBefore(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    List<String> linesBefore = new ArrayList<String>();
    if (lineNumber <= numberOfLines) {
      for (int i = 0;i < lineNumber;i++) {
        linesBefore.add(lines.get(i));
      }
    } else {
      for (int i = numberOfLines;i < lineNumber;i++) {
        linesBefore.add(lines.get(i));
      }
    }
    if (lineNumber < numberOfLines) {
      lineNumber = 0;
    } else {
      lineNumber = lineNumber - numberOfLines;
    }
    Page page=new Page(linesBefore, lineNumber, pageRequest.getFileName(), pageRequest.getCursorAt());
    return page;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting after the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position use the value from pageRequest
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 51, line number 52 ... , line number 74, line number75)

  @Override
  public Page getLinesAfter(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    List<String> linesAfter = new ArrayList<String>();
    if (lines.size() - lineNumber < numberOfLines) {
      for (int i = 1;i < lines.size() - lineNumber;i++) {
        linesAfter.add(lines.get(i+lineNumber));
      }
    } else {
      for (int i = 1;i <= numberOfLines;i++) {
        linesAfter.add(lines.get(i+lineNumber));
      }
    }
    if (lineNumber != lines.size()) {
      lineNumber += 1;
    }
    Page page=new Page(linesAfter, lineNumber, pageRequest.getFileName(), pageRequest.getCursorAt());
    return page;
  }

  // TODO: CRIO_TASK_MODULE_LOAD_FILE
  // Input:
  //    1. lineNumber - The line number
  //    2. numberOfLines - Number of lines requested
  // Expected functionality:
  //    1. Get the requested number of lines starting from the given line number.
  //    2. Make page object of this and return.
  //    3. For cursor position should be (startingLineNo, 0)
  //    4. For fileName use the value from pageRequest
  // NOTE:
  //    If there less than requested number of lines, then return just those lines.
  //    Zero is the first line number of the file  @Override
  // Example:
  //    lineNumber - 50
  //    numberOfLines - 25
  //    Then lines returned is
  //    (line number 50, line number 51 ... , line number 73, line number74)

  @Override
  public Page getLinesFrom(PageRequest pageRequest) {
    int lineNumber = pageRequest.getStartingLineNo();
    int numberOfLines = pageRequest.getNumberOfLines();
    List<String> linesFrom = new ArrayList<String>();
    if (lines.size() - lineNumber <= numberOfLines) {
      for (int i = 0;i < lines.size() - lineNumber;i++) {
        linesFrom.add(lines.get(i+lineNumber));
      }
    } else {
      for (int i = 0;i < numberOfLines;i++) {
        linesFrom.add(lines.get(i+lineNumber));
      }
    }
    Page page=new Page(linesFrom, lineNumber, pageRequest.getFileName(), new Cursor(pageRequest.getStartingLineNo(), 0));
    return page;
  }

  // TODO: CRIO_TASK_MODULE_SEARCH
  // Input:
  //    SearchRequest - contains following information
  //         1. pattern - pattern you want to search
  //         2. File name - file where you want to search for the pattern

  // TODO: CRIO_TASK_MODULE_IMPROVING_SEARCH
  // Input:
  //    SearchRequest - contains following information
  //        1. pattern - pattern you want to search
  //        2. File name - file where you want to search for the pattern
  // Description:
  //    1. Find all occurrences of the pattern in the SourceFile
  //    2. Create an empty list of cursors
  //    3. For each occurrence starting position add to the list of cursors
  //    4. return the list of cursors
  // Recommendation:
  //    1. Use the simplest string search algorithm that you know.
  // Reference:
  //     https://www.geeksforgeeks.org/naive-algorithm-for-pattern-searching/
  //    1. Use FASTER string search algorithm.
  //    2. Feel free to try any other algorithm/data structure to improve search speed.
  // Reference:
  //     https://www.geeksforgeeks.org/kmp-algorithm-for-pattern-searching/
  
  @Override
  public List<Cursor> getCursors(SearchRequest searchRequest) {
    List<Cursor> cursorList = new ArrayList<Cursor>();
    PatternSearchAlgorithm patternSearchAlgorithm = new PatternSearchAlgorithm();
    String pattern = searchRequest.getPattern();
    for (int i = 0;i < lines.size();i++) {
      ArrayList<Integer> columnList = patternSearchAlgorithm.KMPSearch(pattern, lines.get(i));
      for (int j = 0;j < columnList.size();j++) {
        cursorList.add(new Cursor(i,columnList.get(j)));
      } 
    }
    return cursorList;
  }

  @Override
  public List<String> getAllLines() {
    return lines;
  }
}
