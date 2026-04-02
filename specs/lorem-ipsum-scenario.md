BEAT 1: A spec appears
Developer opens context menu: File → New → New Task for Agent (shortcut visible). An empty .md file opens with an inline prompt area at the top.


Narrator: [Explains the task]

The developer attaches some context and writes the prompt using autocomplete. The attached context is demonstrated; it is visibly messy. [More detailed version: "This is a readme of an open-source project that has the functionality I need. Implement it, but adapt to our project realities, for example, AAA is BBB in our project (BBB is autocompleted).]

Enter - spec content streams below the prompt


The prompt area collapses but remains accessible - visually distinct from the spec. Clear affordance: this was the input; the spec below is what matters now. The prompt won't be included in the agent context.


The resulting spec is minimalistic - strictly based on the input the developer gave, no bloated hallucinated extras. Sections: "Goal", "Acceptance Criteria (3 items)", "Plan (a string with execution mode notes + 5 steps)", "Implementation notes" with code references, "Other" with things that were in the context, but our agent was not sure where to place them. In this demo, it will be something small, just one line with something insignificant. It's clear that we're supporting some specific format of the spec, but don't lose your context. The file is already alive, and there's some highlighting.
In the file tree, "spec-task-1.md" appears with a gray icon - idle, never launched. There is also "spec-task-2.md" in the tree, it's marked as running.

The developer expands the prompt, and it's clear he can retype it, but the spec will be regenerated when he hits Enter; no need to create a new file for a new prompt.

Narrator: "If I don't like what came out, I can go back to the prompt and regenerate the whole spec - same file, fresh start. But this looks reasonable, so let's see what's missing."


He minimises prompt back.

BEAT 2: The IDE challenges your spec, and you refine it together
Spec is on screen, it immediately looks alive, code references are clickable, symbols are resolved, and the plan section renders with visual checkboxes and step structure (WYSIWYG-style, not raw markdown).
Next to the editor tabs, a run configuration widget appears - in the same spot and with the same pattern as standard IntelliJ run configs (like "androidApp ▶" in the toolbar). It reads: "spec-task-1 · Run until AC are met (3 max) ▶". The Play button is right there. The config label is derived from the spec's plan section - execution mode and limits. Clicking the dropdown shows: "Refresh spec, "Step-through", "Plan once" - but we don't go there yet.
Play is smart: if there are pending comments or changes that require regeneration, hitting Play will automatically regenerate the spec before launching the agent. The readiness bar shows "Pending regeneration," so it's not hidden magic - the developer always knows what will happen.
Next to the run config, there's Spec readiness bar: "​​X errors · Y warnings · Analysis mode: Fast". Analysis mode can be changed via the dropdown. Issues are shown as standard IDE inspections - yellow/red gutter highlights on specific lines, collapsed by default. No wall of inline text. The readiness bar shows the count; the developer navigates through findings with F2 / arrows, expanding each one with Alt+Enter to see the suggestion and available quick fixes.
Narrator: "I could hit Play right now - nothing stops me. The IDE already picked up the execution mode from the spec. But the readiness bar shows that this spec has issues. As I said, this spec was generated from a suboptimal context, so I would not expect it to be perfect. Let's see what IDE has to say."
The developer navigates through findings one by one via the readiness bar. Each finding expands inline on Alt+Enter, showing the issue, suggestion, and quick-fix options. Unlike a special section in spec, e.g., "Open questions", all findings are inline. Unlike plan mode, you can see them all at once from the readiness bar.
He works through errors and warnings. [We'll showcase 6-7 improvements and 4 interaction modes in total: Direct edit with autocomplete, Accept instant suggestion from IDE (Quick Fix), Respond with comment or selection. We also showcase "decide yourself" and “ignore and suppress” options here. We'll select specific spec improvements when we have the scenario.]
Readiness bar updates as issues are resolved. If something was not just fixed in text, but left as a comment to the generating agent, the readiness bar shows "Pending regeneration". If the developer hits Play now, the spec will be regenerated automatically, then the agent will launch. But there's also "Refresh spec" in the Play dropdown for those who want to see the result first.
Narrator: "Now, if I hit Play, it would regenerate the spec and launch the agent in one go. But let's see what deeper analysis would surface first."
Developer switches analysis mode from "Fast" to "Deep" in the readiness bar dropdown. Inspections re-run automatically - no need to press anything, same as switching an inspection profile in IDE. Deep analysis takes a bit longer and surfaces a couple of more impressive findings, but the interaction modes remain the same.
The developer wants to see the regenerated spec before running the agent. Opens the Play dropdown, picks "Regenerate only". Spec regenerates with all the fixes and comments applied. No agent launch.
In response to some changes, a section titled Tradeoffs appears in the spec, listing important explicit choices the developer or agent made during spec review.
Narrator opens diff for the spec manually, standard IDE local history diff.
Narrator: "I haven't generated a single line of code yet, but we already caught some errors in my context and discrepancies in my intent. All tradeoffs we had to make were recorded; everything is transparent. This is just a file - I can always diff it against any previous version and revert some changes. And the spec still is not bloated, the agent added only the things that really mattered, look at where we started and where we are now."
BEAT 3: Make it work first
The developer looks at the Plan section. It renders with visual structure - checkboxes for steps, icons in the gutter, the execution config line rendered cleanly (not raw markdown syntax). But everything is still editable as text underneath.


Big Play gutter next to the "Plan" header - same Play icon as in the toolbar run config. Each individual step also has a small Play icon in the gutter - same pattern as running a single test method vs. a whole test class. First line with per-plan configuration, it's clear that some of the things here are symbols that were autocompleted: Stop when: all AC met | Max runs: 3. Five steps as checkboxes, no per-step annotations - all default. Gutter icons across each step reflect this: the same icon on every step.


Narrator: "By default, for this task IDE configures the execution as a simple Ralph loop - run until all acceptance criteria pass, max three cycles, it's up to the agent how to validate each AC.”

The developer navigates to the definition of this execution mode string, and we see that this is just an md file in the IDE configuration!


Narrator: “This is just text in the spec - I could run it from a terminal and get the same result. But the IDE suggests to me some validated defaults so I would not have to generate all that, enforces the hooks, and maps execution to the plan in real time. This is a small task, so I'm ok with Ralphing it. Let’s go back."


Developer goes back, hits Play.


Agent log opens at the bottom - like a terminal/build output. Raw execution streams there: step completion and test results.


In the file tree, "spec-task-1.md" changes from gray (idle) to a spinner - agent is running. There is also "spec-task-2.md" in the tree, it's marked as running.

In the spec, steps update live: checkboxes fill in, gutter icons change, and inline references to diffs appear.


AC verification runs automatically. AC status appears: AC 1 ✅ · AC 2 ✅ · AC 3 [Not checked] · AC 4 [Not checked] · AC 5 [Not checked].


Narrator: "We could specify more verification information for each AC - but we did not, so the agent verifies acceptance criteria itself, to its best knowledge - reads the spec, looks at the code, and makes a judgment call."


Loop pauses. IDE sends a notification.


AC status appears: AC 1 ✅ · AC 2 ✅ · AC 3 ✅· AC 4 ⚠️ · AC 5 ✅.

In the file tree, the spinner on "spec-task-1.md" changes to a yellow pause icon - something needs attention.

In the agent log - a line: "Paused - AC 4 requires spec update." It's clickable, like "error in File.kt:47."

But the AC 4 in the spec lights up even more visibly, with the agent's finding and a suggested edit inline - strikethrough on old text, green on new. Accept / Reject right there.


The developer reads the suggestion, clicks Accept. AC 4 updates in place.

Loop resumes. Agent log: Run 2 | Fresh agent | Reading spec... Steps that need rework - re-execute.

File tree: spinner returns on "spec-task-1.md".

AC status: all green. Two runs out of max three. Spinner in the file tree replaced with a green pause sign.

Narrator: "Two runs. I didn't look at a single line of code. Each run started fresh - the spec was the only thing that carried over. The agent even caught something that wasn't visible during spec analysis - it only became clear during implementation."

BEAT 4: Code review, but do it right
File tree: "spec-task-1.md" shows a green pause - all AC passed.
Narrator: "All green. Theoretically, I could merge right now. But to be honest, I don't fully understand the part of the codebase from step [N], and I want to know what I'm owning, so let's do a short code review."

The developer goes to step [N] in the spec. There's a diff link attached; he clicks it, and the diff view opens in a separate tab. Standard IDE diff, nothing custom.

Narrator: "Looks good, but I'd still use a slightly different approach".

Developer leaves a review comment right in the diff view: "use [pattern] here for consistency with [reference]." Switches back to the spec. The comment is already there - attached to plan step [N], flowed automatically from the diff. And the checkbox is unchecked - the spec knows this step needs rework.

Narrator: "I left a comment in the diff, and it appeared on the plan step in my spec. The spec tracks everything - I don't have to."

The developer clicks the small Play icon in the gutter next to step [N] - not the big Play on the Plan header. Just this one step re-executes with the comment as guidance. Same pattern as running a single test method instead of the whole test class. Step re-executes. Checkbox fills back in. The spec logs the decision in Tradeoffs: original approach, developer's comment, and new implementation. AC status goes stale - all five turn gray. Code changed, verification outdated.
File tree: "spec-task-1.md" icon changes from green checkmark to a gray pause - ACs need re-verification.
Narrator: "The acceptance criteria grayed out - they haven't been re-verified since I changed that step. Let me rerun - the agent will see that all steps are done and just re-verify the criteria."

Developer hits Play. Agent log: "All steps complete. Verifying acceptance criteria..."
File tree: spinner returns on "spec-task-1.md". And right below it, "spec-task-2.md" shows a yellow pause icon. It's been running in parallel and is now waiting.
The developer doesn't wait - he switches to the second spec.
BEAT 5: Meanwhile, in another spec
Narrator: "And this wasn't my only task. I can see right in the file tree that task 2 has been paused - waiting for me. For this one, I configured a different execution mode. Let’s take a look."
The toolbar run config widget updates as the developer switches to the second spec's tab: it now reads "spec-task-2 · Step-through ▶" - reflecting a different execution mode. Same Play button, different config.
Scrolls down to the plan section.

Header lines: Stop when: plan complete | After each step: run tests, continue if pass.
Six steps. Steps 1-2 have no annotation next to them (default behavior).
Step 3: run review with @revieweragent, pause.
Steps 4-6: no annotation (default).

Gutter icons reflect the text - same on most steps, step 3 is visually different. Steps 1-2 already done - green checkboxes. This was running in parallel while the developer was on the first task, in a separate branch and worktree. Step 3: done, review ran, paused - waiting for the developer.
Narrator: "No loop here - the agent goes through the plan step by step. Steps 1 and 2 were just simple scaffolding, so running tests should be enough to continue. Step 3 worries me more, so I've asked for a reviewer agent and set a breakpoint at this step."
The developer reads the review findings on step 3 - review agent caught [concern]. The developer leaves a comment on the step to address it.
Then he looks at steps 4-6, still ahead. After the review, he decides to tighten control. Edits step 4 annotation in the spec - types run review, pause (with autocomplete). Gutter icons update to reflect the new text.
Narrator: "The review caught something on step 3. So I'm adding review checkpoints and pause to the next step - just text in the plan, the IDE picks it up."

Developer hits Play to resume. The agent picks up the updated plan.
File tree: "spec-task-2.md" icon changes from yellow pause back to a spinner - running again.
BEAT 6: Merge and capture
The developer returns to the first spec. File tree tells the story before he even switches: "spec-task-1.md" - green pause (done, not merged), "spec-task-2.md" - spinner (still running).
IDE shows a hint: "All acceptance criteria met. Ready to merge."

Narrator: "All green, we're good."
The developer expands the agent log panel a bit and scrolls. Wall of text - raw execution output, step logs, test results, AC verification traces, two full runs worth of agent output.

Narrator: "This is the same task, same agent. This log is what the agent actually did. In a terminal, this would be my whole interface. But with the spec, I didn't need it once."
IDE also suggests extracting learnings - "2 trade-offs recorded. Add to project context?" The developer selects one trade-off, clicks "Add to project context."
Opens AGENTS.md - the trade-off is there. The file lights up with the same inspection tooling: readiness bar, inline findings, and the same gutter highlights. But the Play button in the toolbar is grayed out, and the drop-down is empty - this file has no plan, no steps, nothing to execute. Inspections work automatically, just like for any code file. No button needed.
Narrator: "Same tooling works for all agentic markdown - AGENTS.md, CLAUDE.md, skills, your custom specs, whatever your project uses. Here, it’s just inspections - no need to run anything. And yeah, my project context could use some refinement too. But that's a whole other story."
