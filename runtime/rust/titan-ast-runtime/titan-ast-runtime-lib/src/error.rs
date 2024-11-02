use core::error;
use std::fmt;

use crate::ast::Token;

#[derive(Debug, Clone)]
pub enum AstAppError {
    IoError {
        msg: String,
    },
    TokenParseError {
        finished_tokens: Vec<Token>,
        start: usize,
        end: usize,
        error_text: String,
    },
    TokensError,
    AstParseError {
        start: usize,
        end: usize,
        error_text: String,
    },
    RichTokenParseError {
        finished_tokens: Vec<Token>,
        start_line_number: usize,
        start: usize,
        end_line_number: usize,
        end: usize,
        error_text: String,
    },
    RichAstParseError {
        start_line_number: usize,
        start: usize,
        end_line_number: usize,
        end: usize,
        error_text: String,
    },
}

impl fmt::Display for AstAppError {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        match self {
            AstAppError::IoError { msg } => {
                write!(f, "{}", msg)
            }
            AstAppError::AstParseError {
                start,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "generate ast failed,error near [{},{}):{}",
                    start, end, error_text,
                )
            }
            AstAppError::TokenParseError {
                finished_tokens,
                start,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "[{},{}):'{}' does not match any token",
                    start, end, error_text,
                )
            }
            AstAppError::TokensError => {
                write!(f, "{}", "error in generate tokes")
            }
            AstAppError::RichTokenParseError {
                finished_tokens,
                start_line_number,
                start,
                end_line_number,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "generate ast error,error near [{}-{},{}-{}): '{}'",
                    start_line_number, start, end_line_number, end, error_text
                )
            }
            AstAppError::RichAstParseError {
                start_line_number,
                start,
                end_line_number,
                end,
                error_text,
            } => {
                write!(
                    f,
                    "generate ast error,error near [{}-{},{}-{}): '{}'",
                    start_line_number, start, end_line_number, end, error_text
                )
            }
        }
    }
}

impl From<std::io::Error> for AstAppError {
    fn from(value: std::io::Error) -> Self {
        AstAppError::IoError {
            msg: value.to_string(),
        }
    }
}
