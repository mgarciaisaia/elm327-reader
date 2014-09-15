package elm327.reader;

import com.google.common.base.Strings;

public class ResultParser {

    public static <T> Result<T> parse(String response, Command<T> command) {
        String data;
        try {
            data = extractData(response, command);
        } catch(Exception e) {
            return new Result.Error<T>(command, e);
        }
        return new Result.Response<T>(command, data);
    }

    private static <T> String extractData(String response, Command<T> command) throws Exception {
        String[] fragments = split(response);
        if (fragments.length == 1) {
            return fragments[0];
        }
        if (fragments.length == 2) {
            String message = command.message().code();
            if (message.equals(fragments[0])) {
                return fragments[1];
            }
            throw new BadCommandResultException();
        }
        throw new TooMuchResultDataException();
    }

    private static String[] split(String response) throws Exception {

        if (Strings.isNullOrEmpty(response)) {
            throw new EmptyResultException();
        }

        response = response.trim();

        if (Strings.isNullOrEmpty(response)) {
            throw new EmptyResultException();
        }

        return response.split("\n+");
    }

    @SuppressWarnings("serial")
    public static class EmptyResultException extends Exception {

    }

    @SuppressWarnings("serial")
    public static class BadCommandResultException extends Exception {

    }

    @SuppressWarnings("serial")
    public static class TooMuchResultDataException extends Exception {

    }
}
