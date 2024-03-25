# CookieMonster
 
CookieMonster is a Burp Suite plugin to simplify the filtering of cookies in requests.

The plugin loads cookies from [Open-Cookie-Database](https://github.com/jkwakman/Open-Cookie-Database) which is a collection of categorised cookies. By default, this extension will filter any cookies from the Open-Cookie-Database that are marked as non-functional:

- Preferences
- Analytics
- Marketing

You can see this in action in the screenshots below:

**Before:**

![](/images/cookiemonster-before.png)

**After:**

![](/images/cookiemonster-after.png)

You can also select any text in the request editor and manually send it to CookieMonster to filter out for any custom cookie values.

Any cookie configurations are saved on a per-project basis.

# Wildcard

The plugin uses the behavior described in the Open-Cookie-Database for dynamic cookies. For example:

- If a wildcard filter for `_ga` is enabled, all cookies starting with this value will be filtered ie. `_gac_1234`.

# Limitations

- Currently, there is no way to automatically select the 'Edited Request' in Burp Suite.

# Issues / Pull Requests

For any bugs or feature requests, please raise an [Issue](https://github.com/baegmon/CookieMonster/issues).
