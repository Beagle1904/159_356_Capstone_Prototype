package secapstone.exam;

import secapstone.AbstractDynamoTest;

class ExamSummaryTest extends AbstractDynamoTest {
	protected ExamSummaryTest() {
		super(new String[]{"Questions"}, new String[]{"ID"});
	}
}