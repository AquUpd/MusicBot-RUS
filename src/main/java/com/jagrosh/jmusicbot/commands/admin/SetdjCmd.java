/*
 * Copyright 2018 John Grosh <john.a.grosh@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jagrosh.jmusicbot.commands.admin;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.jagrosh.jmusicbot.Bot;
import com.jagrosh.jmusicbot.commands.AdminCommand;
import com.jagrosh.jmusicbot.settings.Settings;
import com.jagrosh.jmusicbot.utils.FormatUtil;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author John Grosh <john.a.grosh@gmail.com>
 */
public class SetdjCmd extends AdminCommand {

  public SetdjCmd(Bot bot) {
    super(bot);
    this.name = "setdj";
    this.help = "Устанавливает роль для использования DJ команд";
    this.arguments = "<rolename|NONE>";
    this.options = Collections.singletonList(new OptionData(OptionType.ROLE, "rolename", "Роль для DJ команд. NONE чтобы очистить.").setRequired(true));
    this.aliases = bot.getConfig().getAliases(this.name);
  }

  @Override
  protected void execute(SlashCommandEvent event) {
    event.deferReply().queue();
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getOption("rolename").getAsString().equalsIgnoreCase("none")) {
      s.setDJRole(null);
      event.getHook().editOriginal(event.getClient().getSuccess() + " DJ роль очищена; Теперь только администраторы могут её использовать.")
        .queue();
    } else {
      List<Role> list = FinderUtil.findRoles(event.getOption("rolename").getAsString(), event.getGuild());
      if (list.isEmpty())
        event.getHook().editOriginal(event.getClient().getWarning() + " Нет роли с названием \"" + event.getOption("rolename").getAsString() + "\"")
          .queue();
      else if (list.size() > 1)
        event.getHook().editOriginal(event.getClient().getWarning() + FormatUtil.listOfRoles(list, event.getOption("rolename").getAsString()))
          .queue();
      else {
        s.setDJRole(list.get(0));
        event.getHook().editOriginal(event.getClient().getSuccess() + " DJ команды теперь может использовать роль **" + list.get(0).getName() + "**.   ")
          .queue();
      }
    }
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.reply(event.getClient().getError() + " Напишите нужную роль или 'NONE' для очистки(можно написать ID сервера чтобы все могли использовать команду)");
      return;
    }
    Settings s = event.getClient().getSettingsFor(event.getGuild());
    if (event.getArgs().equalsIgnoreCase("none")) {
      s.setDJRole(null);
      event.reply(event.getClient().getSuccess() + " DJ роль очищена; Теперь только администраторы могут её использовать.");
    } else {
      List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
      if (list.isEmpty())
        event.reply(event.getClient().getWarning() + " Нет роли с названием \"" + event.getArgs() + "\"");
      else if (list.size() > 1)
        event.reply(event.getClient().getWarning() + FormatUtil.listOfRoles(list, event.getArgs()));
      else {
        s.setDJRole(list.get(0));
        event.reply(event.getClient().getSuccess() + " DJ команды теперь может использовать роль **" + list.get(0).getName() + "**.   ");
      }
    }
  }
}
