//
// Created by tian wei jun on 2024/1/16.
//

#ifndef AST_RUNTIME_RUNTIME_ASTAUTOMATATYPE_H_
#define AST_RUNTIME_RUNTIME_ASTAUTOMATATYPE_H_
enum class AstAutomataType : int {
  BACKTRACKING_BOTTOM_UP_AST_AUTOMATA = 0,
  FOLLOW_FILTER_BACKTRACKING_BOTTOM_UP_AST_AUTOMATA = 1
};
#endif // AST_RUNTIME_RUNTIME_ASTAUTOMATATYPE_H_