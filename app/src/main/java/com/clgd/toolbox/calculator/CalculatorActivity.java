package com.clgd.toolbox.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.clgd.toolbox.R;
import com.clgd.toolbox.calculator.utils.ParseExpression;

import java.lang.reflect.Method;


/**
 * Created by hwl on 2017/09/08.
 */

public class CalculatorActivity extends Activity {

    private String parenthesis = "( )";
    private String regOperator = "\\+|-|×|÷";

    private char lastChar = ' ';

    private EditText printET;
    private Class<EditText> cls;
    private Method method;

    private Button bt;
    private String btText;
    private String exp;
    private String expAndResult;
    private String frontExp = exp;    //光标前的表达式,默认光标在尾端
    private String rearExp = "";    //光标后的表达式,默认光标在尾端
    private String inputString;
    private String resultString;
    private boolean cursorEnd = true;    //光标是否在尾端，默认为真
    private String penultCharString;
    private StringBuilder historySB = new StringBuilder();    //保存历史记录


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calculator);

        printET = (EditText) findViewById(R.id.printET);
        disableShowSoftInput(printET);
        initVal();

    }

    private void initVal() {
        resultString = "";
        exp = "";
        expAndResult = "";
        frontExp = exp;
        rearExp = "";
        cursorEnd = true;
        printET.setText("");
        printET.setSelection(exp.length());
    }

    public void onClick(View view) {
        bt = (Button) view;
        btText = bt.getText().toString();

        exp = printET.getText().toString();
        expAndResult = "";
        frontExp = exp;
        rearExp = "";
        inputString = bt.getText().toString();

        if (resultString.matches(getResources().getString(R.string.calculator_invalid_format))) {
            initVal();
        }

        cursorEnd = true;
        int cursorIndex = printET.getSelectionStart();
        if (cursorIndex != printET.getText().length()) {
            cursorEnd = false;
        }

        if (!cursorEnd) {
            if (resultString == "") {
                if ((exp.length() > 0)) {
                    if (cursorIndex > 0) {
                        frontExp = exp.substring(0, cursorIndex);
                        rearExp = exp.substring(cursorIndex, exp.length());
                    } else if (cursorIndex == 0) {
                        frontExp = "";
                        rearExp = exp;
                    }
                }
            } else {
                initVal();
            }
        }

        if (btText.matches("[0-9]|\\+|-|×|÷|\\.|(\\( \\))|=")) {
            //已经运算过一次并有了运算结果
            if ((resultString != "")) {
                //判断光标位置
                if (cursorEnd) {    //光标在尾端
                    if ( ParseExpression.isOperator(inputString)) {    //如果输入的是运算符，表明是继续运算
                        exp = resultString;
                        resultString = "";
                    } else if (inputString.matches("=")) {    //若输入"="，继续重复上一次的运算符运算
                        exp = exp.replaceAll("\n=.*", "");
                        String[] op = ParseExpression.splitInfixExp(exp);
                        int opLen = op.length;
                        if (opLen < 2) {
                            return;
                        }
                        exp = resultString + op[opLen - 2] + op[opLen - 1];
                        resultString = "";
                    } else if (inputString == parenthesis) {    //若输入括号
                        exp = "(" + resultString;
                        inputString = "";
                        resultString = "";
                    } else {    //重新开始新的运算
                        initVal();
                    }
                } else {    //光标不在尾端，则开始新的运算
                    initVal();
                }
            }

            if (inputString.matches("\\.")) {
                if (cursorEnd) {
                    if (ParseExpression.appendDotValid(exp)) {
                        if (exp.matches(".*?(" + regOperator + "|\\()$|()")) {
                            inputString = "0.";
                        }
                    } else {
                        return;
                    }
                } else {
                    if (ParseExpression.appendDotValid(frontExp)) {
                        if (frontExp.matches(".*?(" + regOperator + "|\\()$|()")) {
                            inputString = "0.";
                        } else {
                            inputString = ".";
                        }
                    } else {
                        return;
                    }
                }
            } else if (cursorEnd) {
                if (inputString == parenthesis) {
                    inputString = ParseExpression.inputParenthesis(exp);
                    if (inputString.matches("\\)")) {
                        if (exp.length() > 1) {
                            lastChar = exp.charAt(exp.length() - 1);
                        }
                        if (lastChar == '.') {
                            exp = exp.substring(0, exp.length() - 1);
                        }
                    }
                } else if (ParseExpression.isOperator(inputString)) {
                    if (exp.endsWith("(")) {
                        if (!inputString.matches("-")) {
                            return;
                        }
                    }
                    //如果lastCHar是运算符且倒数第二个字符是数字，则替换成input输入的运算符
                    else if (exp.length() > 1) {
                        lastChar = exp.charAt(exp.length() - 1);
                        penultCharString = String.valueOf(exp.charAt(exp.length() - 2));
                        if (ParseExpression.isOperator(lastChar)) {
                            if (penultCharString.matches("[0-9]|\\)|\\.|%")) {
                                exp = exp.substring(0, exp.length() - 1);
                            } else {
                                return;
                            }
                        } else if ((lastChar == '.')) {
                            exp = exp.substring(0, exp.length() - 1);
                        }
                    } else if (exp.length() == 1) {
                        if (exp.matches("-")) {
                            return;
                        }
                    } else if (!inputString.matches("-")) {
                        return;
                    }
                } else if (inputString.matches("[0-9]")) {
                    if ((exp.length() > 0)) {
                        lastChar = exp.charAt(exp.length() - 1);
                        if ((lastChar == ')') || (lastChar == '%')) {
                            return;
                        }
                    }
                    if (exp.endsWith("0")) {
                        if (exp.length() == 1) {
                            exp = "";
                        } else {
                            exp = exp.replaceAll("(.*?)(" + regOperator + "|\\()(0)$", "$1$2");
                        }
                    }
                }
                lastChar = ' ';
            }

            if (inputString.equals("=")) {
                //若表达式以运算符结尾，则去掉末端的运算符 再执行运算
                exp = exp.replaceAll("(.*?)(\\+|-|×|÷)(\\(?)$", "$1");
                if (!ParseExpression.isParenthesisMatch(exp)) {
                    return;
                }
                if (exp == "") {
                    return;
                }

                resultString = ParseExpression.calInfix(exp);
                expAndResult = exp + "\n=" + resultString;
                historySB.append(expAndResult.replace("\n", ",") + ";");
                printET.setText(expAndResult);
            } else {
                if (cursorEnd) {
                    exp = exp + inputString;
                } else {
                    if (inputString == parenthesis) {
                        inputString = "()";
                    }
                    exp = frontExp + inputString + rearExp;
                }
                printET.setText("");
                printET.setText(exp);
            }
        } else if (btText.matches("%")) {
            if (cursorEnd) {
                if ((resultString != "")) {
                    exp = resultString;
                    resultString = "";
                } else if (!exp.matches(".*?(\\)|[0-9])$")) {
                    return;
                }
                printET.setText(exp + "%");
            } else {
                if ((resultString != "")) {
                    return;
                }
                printET.setText(frontExp + "%" + rearExp);
            }
        } else if (btText.matches("C")) {
            initVal();
            return;
        } else if (btText.matches("D")) {

            if (frontExp.equals("")) {
                return;
            }

            if (resultString != "") {
                initVal();
                return;
            }

            frontExp = frontExp.substring(0, frontExp.length() - 1);
            printET.setText(frontExp + rearExp);
            printET.setSelection(cursorIndex - 1);
            return;
        }

        if (cursorEnd || (inputString == "=")) {
            printET.setSelection(printET.getText().length());
        } else if (!cursorEnd && inputString.matches("(\\(\\))|(0\\.)")) {
            printET.setSelection(cursorIndex + 2);
        } else if (cursorIndex < printET.getText().length()) {
            printET.setSelection(cursorIndex + 1);
        }
    }

    public void disableShowSoftInput(EditText editText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.setInputType(InputType.TYPE_NULL);  //强制关闭软键盘，但是编辑框没有闪烁的光标
        } else {  //android3.0版本以上才能使用
            //设置输入模式，窗体获得焦点，始终隐藏软键盘
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            cls = EditText.class;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);  //设置是可访问，为true，表示禁止访问
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                method = cls.getMethod("setSoftInputShownOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
